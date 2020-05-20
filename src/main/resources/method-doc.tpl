    /**
     * Execute the ${method.name} Transaction.
     *
#foreach( $param in ${method.parameters} )
     * @param ${param.javaName} ${param.type}.
#end
#if( ${method.files} )
     * @param files an array of UploadFiles.
#end
     * @return ${method.returnDoc} containing the response.
     * @throws SimbaException if an error occurs. 
     */