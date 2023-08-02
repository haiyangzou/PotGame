package org.pot.core.engine;

@HandlerDefinition
public interface Handler<ObjectType, RequestType, ResultType> {
    @HandlerDefinition
    ResultType handle(ObjectType objectType, RequestType requestType) throws Exception;

    Class<ObjectType> getObjectType();

    Class<RequestType> getRequestType();

    Class<ResultType> getResultType();
}
