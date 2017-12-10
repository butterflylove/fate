package destiny.fate.common.config.loader;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;


/**
 * @author zhangtianlong
 */
public class XMLServerLoader {

    private static void load() throws DocumentException {
        // 读取server.xml配置
        InputStream is = XMLServerLoader.class.getResourceAsStream("/server.xml");
        SAXReader reader = new SAXReader();
        Document doc = reader.read(is);
        Element root = doc.getRootElement();
        Iterator<Element> it = root.elementIterator();

        while (it.hasNext()) {
            Element userElem= it.next();
            List<Attribute> attributeList = userElem.attributes();
            for (Attribute attribute : attributeList) {
                String name = attribute.getName();
                String value = attribute.getValue();
                System.out.println(name + ":" + value);
            }
            Iterator<Element> it2 = userElem.elementIterator();
            while (it2.hasNext()) {
                Element element = it2.next();
                String name = element.getName();
                String text = element.getText();
                System.out.println(name + "" + text);
            }
        }
    }

    public static void main(String[] args) throws DocumentException {
        load();
    }
}
