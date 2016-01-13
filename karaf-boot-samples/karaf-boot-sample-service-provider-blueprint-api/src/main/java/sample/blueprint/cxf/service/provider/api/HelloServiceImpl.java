package sample.blueprint.cxf.service.provider.api;

public class HelloServiceImpl implements HelloService {

    public String sayHello(String message) {
        return "Hello " + message;
    }
}
