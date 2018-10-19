package test.fuse.cxf.jaxrs.undertow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * A dummy implementation of {@link OrderService} to use for testing and running this example.
 */
@Singleton
@Named("orderService")
public class DummyOrderService implements OrderService {
    private Map<Integer, Order> orders = new HashMap<>();

    private final AtomicInteger idGen = new AtomicInteger();

    public DummyOrderService() {
        setupDummyOrders();
    }

    @Override
    public Order getOrder(int orderId) {
        return orders.get(orderId);
    }

    private String createOrder(Order order) {
        int id = idGen.incrementAndGet();
        order.setId(id);
        orders.put(id, order);
        return "" + id;
    }

    public void setupDummyOrders() {
        Order order = new Order();
        order.setAmount(1);
        order.setPartName("motor");
        order.setCustomerName("honda");
        createOrder(order);

        order = new Order();
        order.setAmount(3);
        order.setPartName("brake");
        order.setCustomerName("toyota");
        createOrder(order);
    }
    
}
