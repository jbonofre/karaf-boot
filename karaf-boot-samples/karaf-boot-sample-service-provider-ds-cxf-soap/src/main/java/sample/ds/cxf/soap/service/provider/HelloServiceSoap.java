package sample.ds.cxf.soap.service.provider;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://boot.karaf.apache.com", name = "Karaf-boot-ds")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface HelloServiceSoap {

    @WebResult(targetNamespace = "http://boot.karaf.apache.com")
    @WebMethod(operationName = "Hello", action = "Hello")
    public String Hello(String name);
}
