package hu.tilos.radio;

import hu.radio.tilos.model.ListenerStat;
import org.apache.camel.Exchange;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class StatTransformer {

    Map<String, Integer> typeCode = new HashMap<>();

    public StatTransformer() {
        typeCode.put("tilos_high", 1);
    }

    public void transform(Exchange exchange) {
        Node typeNode = ((NodeList) exchange.getIn().getHeader("type")).item(0);
        String type = typeNode == null ? null : typeNode.getTextContent();
        NodeList count = (NodeList) exchange.getIn().getHeader("count");
        ListenerStat stat = new ListenerStat();
        stat.setCount(Integer.parseInt(count.item(0).getTextContent()));
        if (typeCode.containsKey(type)) {
            stat.setType(typeCode.get(type));
        }
      //  stat.setDate((java.util.Date) exchange.getIn().getHeader("timestamp"));
        System.out.println(stat);

    }
}
