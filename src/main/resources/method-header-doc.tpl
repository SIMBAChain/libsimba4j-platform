    /**
     * Execute the ${method.name} Transaction.
     *
#foreach( $param in ${method.parameters} )
     * @param ${param.javaName} ${param.type}.
#end
     * @param ${method.headersName} headers to include in the call.
#if( ${method.files} )
     * @param files an array of UploadFiles.
#end
     * @return ${method.returnDoc} containing the response.
     * @throws SimbaException if an error occurs. 
     */