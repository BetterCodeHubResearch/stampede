/*
 * This file is part of ToroDB.
 *
 * ToroDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ToroDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with repl. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2016 8Kdata.
 * 
 */

package com.torodb.mongodb.repl.commands;

import com.eightkdata.mongowp.bson.BsonDocument;
import com.eightkdata.mongowp.exceptions.*;
import com.eightkdata.mongowp.server.api.MarshalException;
import com.eightkdata.mongowp.server.api.impl.AbstractNotAliasableCommand;
import com.eightkdata.mongowp.server.api.tools.Empty;

/**
 * The command to which all not supported commands that must stop the server 
 * when found are mapped.
 *
 * @see ReplCommandsLibrary
 */
public class LogAndStopCommand extends AbstractNotAliasableCommand<String, Empty> {

    public static final LogAndStopCommand INSTANCE = new LogAndStopCommand();

    private LogAndStopCommand() {
        super("log-and-stop");
    }

    @Override
    public Class<? extends String> getArgClass() {
        return String.class;
    }

    @Override
    public String unmarshallArg(BsonDocument requestDoc) throws MongoException {
        return requestDoc.getFirstEntry().getKey();
    }

    @Override
    public BsonDocument marshallArg(String request) throws MarshalException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<? extends Empty> getResultClass() {
        return Empty.class;
    }

    @Override
    public Empty unmarshallResult(BsonDocument resultDoc) throws
            BadValueException, TypesMismatchException, NoSuchKeyException,
            FailedToParseException, MongoException {
        return Empty.getInstance();
    }

    @Override
    public BsonDocument marshallResult(Empty result) throws MarshalException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
