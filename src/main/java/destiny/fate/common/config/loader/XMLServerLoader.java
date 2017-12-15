package destiny.fate.common.config.loader;

import destiny.fate.common.config.model.UserConfig;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 加载 server.xml
 *
 * @author zhangtianlong
 */
public class XMLServerLoader {

    private static final Logger logger = LoggerFactory.getLogger(XMLServerLoader.class);

    private static final String NAME = "name";

    private static UserConfig userConfig = new UserConfig();

    static  {
        try {
            load();
        } catch (DocumentException e) {
            logger.error("server.xml loading error!");
            e.printStackTrace();
        }

    }

    public static UserConfig getUserConfig() {
        return userConfig;
    }

    private static Map<String, String> getAttributes(List<Element> elements) {
        Map<String, String> map = new HashMap<String, String>();
        if (elements != null) {
            for (Element element : elements) {
                String text = element.getText();
                List<Attribute> list = element.attributes();
                for (Attribute attribute : list) {
                    String name = attribute.getName();
                    if (NAME.equals(name)) {
                        map.put(attribute.getValue(), text);
                    }
                }
            }
        }
        return map;
    }

    private static void load() throws DocumentException {
        // 读取server.xml配置
        InputStream is = XMLServerLoader.class.getResourceAsStream("/server.xml");
        SAXReader reader = new SAXReader();
        Document doc = reader.read(is);
        Element root = doc.getRootElement();
        Iterator<Element> it = root.elementIterator();

        while (it.hasNext()) {
            Element userElem= it.next();
            Map<String, String> map = getAttributes(userElem.elements());
            userConfig.setName(map.get("name"));
            userConfig.setPassword(map.get("password"));
            userConfig.setSchema(map.get("schema"));
            break;
        }
    }

    public static void main(String[] args) throws DocumentException {
        System.out.print(userConfig);
    }
}
