/*
 *     This file is part of ToroDB.
 *
 *     ToroDB is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ToroDB is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with ToroDB. If not, see <http://www.gnu.org/licenses/>.
 *
 *     Copyright (c) 2014, 8Kdata Technology
 *     
 */

package com.torodb.backend;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.torodb.backend.meta.SchemaUpdater;
import com.torodb.backend.meta.SnapshotUpdater;
import com.torodb.core.TableRefFactory;
import com.torodb.core.guice.CoreModule;
import com.torodb.core.impl.TableRefFactoryImpl;
import com.torodb.core.transaction.metainf.ImmutableMetaSnapshot;
import com.torodb.core.transaction.metainf.MetainfoRepository.SnapshotStage;
import com.torodb.metainfo.cache.mvcc.MvccMetainfoRepository;

public abstract class AbstractBackendTest {
    
    protected static final TableRefFactory tableRefFactory = new TableRefFactoryImpl();
    
    protected SqlInterface sqlInterface;
    private SchemaUpdater schemaUpdater;
    private SqlHelper sqlHelper;
    
    @Before
    public void setUp() throws Exception {
    	DatabaseForTest database = getDatabaseForTest();
    	
        Injector injector = createInjector(database);
        DbBackendService dbBackend = injector.getInstance(DbBackendService.class);
        dbBackend.startAsync();
        dbBackend.awaitRunning();
        database.cleanDatabase(injector);
        sqlInterface = injector.getInstance(SqlInterface.class);
        sqlHelper = injector.getInstance(SqlHelper.class);
        schemaUpdater = injector.getInstance(SchemaUpdater.class);
    }
    
    private Injector createInjector(DatabaseForTest database){
        Module sqlModule = new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bind(SqlInterface.class)
                    .to(SqlInterfaceDelegate.class)
                    .in(Singleton.class);
                binder.bind(DslContextFactory.class)
                    .to(DslContextFactoryImpl.class)
                    .asEagerSingleton();
            }
        };
    	
    	List<Module> modules = new ArrayList<>();
    	modules.add(new CoreModule());
    	modules.add(sqlModule);
    	modules.addAll(database.getModules());
        return Guice.createInjector(modules.toArray(new Module[]{}));
    }
    
    protected abstract DatabaseForTest getDatabaseForTest();
    
    protected ImmutableMetaSnapshot buildMetaSnapshot() {
        MvccMetainfoRepository metainfoRepository = new MvccMetainfoRepository();
        SnapshotUpdater.updateSnapshot(metainfoRepository, sqlInterface, sqlHelper, schemaUpdater, tableRefFactory);

        try (SnapshotStage stage = metainfoRepository.startSnapshotStage()) {
            return stage.createImmutableSnapshot();
        }
    }
    
}
