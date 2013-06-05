/*
 * @(#)$Id$
 *
 * copied from Java(tm)-Spektrum 2/2001, p.56ff
 */
package de.bo.mediknight.widgets;

import java.util.*;
import java.util.List;
import java.io.*;
import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.lang.reflect.*;

/**
 * this class implements a Look-and-Feel customizer that has the ability to read
 * in key/value pairs for widget attributes from a file and customize a tree
 * of widgets using these. From: Java(tm)-Spektrum 2/2001, p.56ff
 *
 * @author chs@baltic-online.de
 * @version 1.0
 */
public class LookAndFeelCustomizer {

    public static final int LOOK_AND_FEEL = 0,
                            COLOR = 1,
                            STRING = 2,
                            INTEGER = 3,
                            INSETS = 4,
                            DIMENSION = 5,
                            ICON = 6,
                            FONT = 7,
                            BOOLEAN = 8;

    public static final List<String> PREFIXES = Arrays.asList(new String[] {
        "*", "TextField", "TextArea", "Panel", "Label", "Button", "CheckBox",
        "ComboBox", "List", "OptionPane", "FileChooser", "ColorChooser",
        "TitledBorder", "Table", "TableHeader", "Tree", "Desktop", "FileView",
        "InternalFrame", "MenuItem", "ProgressBar", "Scrollbar", "ScrollPane",
        "SplitPane", "TabbedPane", "Separator", "Slider", "ToolBar" });

    private HashMap<String, Object> defaultProperties = new HashMap<String, Object>();
    private String rootName;

    public void loadDefaults(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        String line = null;

        while((line = in.readLine()) != null) {
            if(line.startsWith("//") || line.equals("")) continue;

            int separatorIndex = line.indexOf("=");
            if(separatorIndex < 0) continue;

            String resourceKey = line.substring(0, separatorIndex);
            String resourceValueString = line.substring(separatorIndex + 1);
            Object resourceValue = null;
            int resourceType = determineResourceType(resourceKey);

            try {
                StringTokenizer st = new StringTokenizer(resourceValueString, ",");

                switch(resourceType) {
                    case LOOK_AND_FEEL:
                        resourceValue = new ColorUIResource(new Color(
                                            Integer.parseInt(st.nextToken()),
                                            Integer.parseInt(st.nextToken()),
                                            Integer.parseInt(st.nextToken()),
                                            Integer.parseInt(st.nextToken())));
                        break;
                    case STRING:
                        resourceValue = st.nextToken();
                        break;
                    case INTEGER:
                        resourceValue = new Integer(st.nextToken());
                        break;
                    case INSETS:
                        resourceValue = new InsetsUIResource(
                                            Integer.parseInt(st.nextToken()),
                                            Integer.parseInt(st.nextToken()),
                                            Integer.parseInt(st.nextToken()),
                                            Integer.parseInt(st.nextToken()));
                        break;
                    case DIMENSION:
                        resourceValue = new DimensionUIResource(
                                            Integer.parseInt(st.nextToken()),
                                            Integer.parseInt(st.nextToken()));
                        break;
                    case ICON:
                        resourceValue = new IconUIResource(
                                            new ImageIcon(st.nextToken()));
                        break;
                    case FONT:
                        resourceValue = new FontUIResource(new Font(
                                            st.nextToken(),
                                            Integer.parseInt(st.nextToken()),
                                            Integer.parseInt(st.nextToken())));
                        break;
                    case BOOLEAN:
                        resourceValue = new Boolean(
                                            st.nextToken().equalsIgnoreCase("true"));
                }

                if(resourceValue == null) continue;
                if(isSystemProperty(resourceKey)) {
                    if(resourceKey.startsWith("*.")) {

                        int size = PREFIXES.size();
                        for(int i = 1; i < size; i++) {
                            String expandedResourceKey = PREFIXES.get(i) + resourceKey.substring(resourceKey.indexOf('.'));
                            if(UIManager.get(expandedResourceKey) != null)
                                UIManager.put(expandedResourceKey, resourceValue);
                        }

                    } else
                        UIManager.put(resourceKey, resourceValue);

                } else {
                    StringTokenizer st2 = new StringTokenizer(resourceKey, ".");
                    int tokenCount = st2.countTokens();
                    HashMap<String, Object> map = defaultProperties;

                    for(int i = 1; i < tokenCount; i++) {
                        String name = st2.nextToken();

                        if(i == 1 && rootName == null)
                            rootName = name;

                        HashMap<String, Object> mapForName = (HashMap<String, Object>) map.get(name);
                        if(mapForName == null)
                            map.put(name, mapForName = new HashMap<String, Object>());
                        map = mapForName;

                    }
                    map.put(st2.nextToken(), resourceValue);
                }

            } catch (Exception ex) {
                System.out.println("couldn't understand\"" + line + "\"");
            }
        }
        
        in.close();
    }

    public void customizeUITree(JComponent root) {
        if(rootName == null) return;
        HashMap<String, Object> rootProperties = (HashMap<String, Object>) defaultProperties.get(rootName);
        customizeUITree(root, cloneIfNecessary(new HashMap<String, Object>(), rootProperties,
                            PREFIXES, false), rootProperties);
    }

    public void customizeComponent(JComponent component) {
        if(rootName == null) return;
        String propertyKey = getPropertyKey(component);

        HashMap<String, Object> defaultProperties = new HashMap<String, Object>();
        defaultProperties.put(rootName, this.defaultProperties.get(rootName));

        HashMap<String, Object> defaultPropertiesForComponent =
            computeDefaultProperties(component, new HashMap<String, Object>(), defaultProperties,
            Arrays.asList(new String[] { "*", propertyKey}));

        if(!defaultPropertiesForComponent.isEmpty())
            customizeComponent(component, defaultPropertiesForComponent);
    }

    protected void customizeUITree(JComponent root, HashMap<String, Object> defaultProperties, HashMap<String, Object> specialProperties) {
        customizeComponent(root, defaultProperties);
        int componentCount = root.getComponentCount();

        for(int index = 0; index < componentCount; index++) {
            Component c = root.getComponent(index);
            if(!(c instanceof JComponent)) continue;

            JComponent child = (JComponent) c;
            HashMap<String, Object> childProperties = (specialProperties == null) ? null :
                (HashMap<String, Object>) specialProperties.get(child.getName());
            customizeUITree(child, cloneIfNecessary(defaultProperties,
                childProperties, PREFIXES, true), childProperties);

        }
    }

    protected void customizeComponent(JComponent component, HashMap<String, Object> defaultProperties) {
        HashMap<String, Object> commonProperties = (HashMap<String, Object>) defaultProperties.get("*");
        HashMap<String, Object> specialProperties = (HashMap<String, Object>) defaultProperties.get(getPropertyKey(component));
        HashMap<String, Object> componentProperties = null;

        if(commonProperties == null)
            componentProperties = specialProperties;
        else {
            componentProperties = (HashMap<String, Object>) commonProperties.clone();
            if(specialProperties != null)
                componentProperties.putAll(specialProperties);
        }

        if(componentProperties == null) return;
        Iterator<String> keys = componentProperties.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            customizeProperty(component, key, componentProperties.get(key));
        }
    }

    protected void customizeProperty(JComponent component, String key, Object value) {
        if(value == null || value instanceof HashMap) return;
        try {
            Class<? extends JComponent> componentClass = component.getClass();
            Method propertySetter = componentClass.getMethod(
                getPropertySetterName(key),
                new Class[] { value.getClass().getSuperclass() }
            );

            propertySetter.invoke(component, new Object[] { value });
        } catch (Exception ex) {
            System.out.println("unable to set property");
        }
    }

    protected HashMap<String, Object> cloneIfNecessary(HashMap<String, Object> defaultProperties,
        HashMap<String, Object> childProperties, java.util.List<String> keys, boolean isCloningNecessary) {

        if(childProperties == null || childProperties.isEmpty())
            return defaultProperties;

        HashMap<String, Object> newDefaultProperties = null;
        int size = keys.size();
        for(int i = 0; i < size; i++) {
            String prefixKey = keys.get(i);
            HashMap<String, Object> newPropertiesForPrefix = (HashMap<String, Object>) childProperties.get(prefixKey);

            if(newPropertiesForPrefix != null) {

                if(newDefaultProperties == null)
                    if(isCloningNecessary)
                        newDefaultProperties = (HashMap<String, Object>) defaultProperties.clone();
                    else
                        newDefaultProperties = defaultProperties;

                HashMap<String, Object> oldPropertiesForPrefix = (HashMap<String, Object>) defaultProperties.get(prefixKey);
                if(oldPropertiesForPrefix != null)
                    oldPropertiesForPrefix.putAll(newPropertiesForPrefix);
                else
                    newDefaultProperties.put(prefixKey, newPropertiesForPrefix);

            }
        }

        if(newDefaultProperties == null)
            return defaultProperties;
        else
            return newDefaultProperties;

    }

    protected HashMap<String, Object> computeDefaultProperties(JComponent component,
        HashMap<String, Object> defaultPropertiesForComponent, HashMap<String, Object> defaultProperties,
        java.util.List<String> keys) {

        String name = component.getName();
        if(rootName.equals(name))
            return cloneIfNecessary(defaultPropertiesForComponent, (HashMap<String, Object>)
                defaultProperties.get(rootName), keys, false);
        else {

            JComponent parent = (JComponent) component.getParent();
            HashMap<String, Object> defaultPropertiesForParent = computeDefaultProperties(parent,
                defaultPropertiesForComponent, defaultProperties, keys);
            if(defaultProperties.isEmpty())
                return defaultPropertiesForParent;

            HashMap<String, Object> defaultPropertiesForChild = (HashMap<String, Object>) ((HashMap<String, Object>)
                defaultProperties.get(parent.getName())).get(name);
            defaultProperties.clear();
            if(defaultPropertiesForChild == null)
                return defaultPropertiesForParent;

            defaultProperties.put(name, defaultPropertiesForChild);
            return cloneIfNecessary(defaultPropertiesForParent,
                defaultPropertiesForChild, keys, false);
        }
    }

    protected String getPropertyKey(JComponent component) {
        String key = component.getClass().getName();
        key = key.substring(key.lastIndexOf('.') + 1);
        if(key.startsWith("J")) key = key.substring(1);
        return key;
    }

    protected String getPropertySetterName(String propertyKey) {
        return "set" + ((char) propertyKey.charAt(0) - 'a' + 'A') + propertyKey.substring(1);
    }

    protected int determineResourceType(String resourceKey) {
        String type = resourceKey.toLowerCase();
        if(type.equals("lookandfeel")) return LOOK_AND_FEEL;
        if(type.endsWith("font")) return FONT;
        if(type.endsWith("color")) return COLOR;
        if(type.endsWith("string")) return STRING;
        if(type.endsWith("integer")) return INTEGER;
        if(type.endsWith("insets")) return INSETS;
        if(type.endsWith("dimension")) return DIMENSION;
        if(type.endsWith("icon")) return ICON;
        return BOOLEAN;
    }

    protected boolean isSystemProperty(String resourceKey) {
        StringTokenizer st = new StringTokenizer(resourceKey, ".");
        int tokenCount = st.countTokens();
        if(tokenCount == 1) return true;
        if(tokenCount == 2 && PREFIXES.contains(st.nextToken())) return true;
        return false;
    }
}
