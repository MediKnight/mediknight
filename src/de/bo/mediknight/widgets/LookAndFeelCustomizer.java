/*
 * @(#)$Id$
 *
 * copied from Java(tm)-Spektrum 2/2001, p.56ff
 */
package de.bo.mediknight.widgets;

import java.util.*;
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

    public static final java.util.List PREFIXES = Arrays.asList(new String[] {
        "*", "TextField", "TextArea", "Panel", "Label", "Button", "CheckBox",
        "ComboBox", "List", "OptionPane", "FileChooser", "ColorChooser",
        "TitledBorder", "Table", "TableHeader", "Tree", "Desktop", "FileView",
        "InternalFrame", "MenuItem", "ProgressBar", "Scrollbar", "ScrollPane",
        "SplitPane", "TabbedPane", "Separator", "Slider", "ToolBar" });

    private HashMap defaultProperties = new HashMap();
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
                    HashMap map = defaultProperties;

                    for(int i = 1; i < tokenCount; i++) {
                        String name = st2.nextToken();

                        if(i == 1 && rootName == null)
                            rootName = name;

                        HashMap mapForName = (HashMap) map.get(name);
                        if(mapForName == null)
                            map.put(name, mapForName = new HashMap());
                        map = mapForName;

                    }
                    map.put(st2.nextToken(), resourceValue);
                }

            } catch (Exception ex) {
                System.out.println("couldn't understand\"" + line + "\"");
            }
        }
    }

    public void customizeUITree(JComponent root) {
        if(rootName == null) return;
        HashMap rootProperties = (HashMap) defaultProperties.get(rootName);
        customizeUITree(root, cloneIfNecessary(new HashMap(), rootProperties,
                            PREFIXES, false), rootProperties);
    }

    public void customizeComponent(JComponent component) {
        if(rootName == null) return;
        String propertyKey = getPropertyKey(component);

        HashMap defaultProperties = new HashMap();
        defaultProperties.put(rootName, this.defaultProperties.get(rootName));

        HashMap defaultPropertiesForComponent =
            computeDefaultProperties(component, new HashMap(), defaultProperties,
            Arrays.asList(new String[] { "*", propertyKey}));

        if(!defaultPropertiesForComponent.isEmpty())
            customizeComponent(component, defaultPropertiesForComponent);
    }

    protected void customizeUITree(JComponent root, HashMap defaultProperties, HashMap specialProperties) {
        customizeComponent(root, defaultProperties);
        int componentCount = root.getComponentCount();

        for(int index = 0; index < componentCount; index++) {
            Component c = root.getComponent(index);
            if(!(c instanceof JComponent)) continue;

            JComponent child = (JComponent) c;
            HashMap childProperties = (specialProperties == null) ? null :
                (HashMap) specialProperties.get(child.getName());
            customizeUITree(child, cloneIfNecessary(defaultProperties,
                childProperties, PREFIXES, true), childProperties);

        }
    }

    protected void customizeComponent(JComponent component, HashMap defaultProperties) {
        HashMap commonProperties = (HashMap) defaultProperties.get("*");
        HashMap specialProperties = (HashMap) defaultProperties.get(getPropertyKey(component));
        HashMap componentProperties = null;

        if(commonProperties == null)
            componentProperties = specialProperties;
        else {
            componentProperties = (HashMap) commonProperties.clone();
            if(specialProperties != null)
                componentProperties.putAll(specialProperties);
        }

        if(componentProperties == null) return;
        Iterator keys = componentProperties.keySet().iterator();
        while(keys.hasNext()) {
            String key = (String) keys.next();
            customizeProperty(component, key, componentProperties.get(key));
        }
    }

    protected void customizeProperty(JComponent component, String key, Object value) {
        if(value == null || value instanceof HashMap) return;
        try {
            Class componentClass = component.getClass();
            Method propertySetter = componentClass.getMethod(
                getPropertySetterName(key),
                new Class[] { value.getClass().getSuperclass() }
            );

            propertySetter.invoke(component, new Object[] { value });
        } catch (Exception ex) {
            System.out.println("unable to set property");
        }
    }

    protected HashMap cloneIfNecessary(HashMap defaultProperties,
        HashMap childProperties, java.util.List keys, boolean isCloningNecessary) {

        if(childProperties == null || childProperties.isEmpty())
            return defaultProperties;

        HashMap newDefaultProperties = null;
        int size = keys.size();
        for(int i = 0; i < size; i++) {
            String prefixKey = (String) keys.get(i);
            HashMap newPropertiesForPrefix = (HashMap) childProperties.get(prefixKey);

            if(newPropertiesForPrefix != null) {

                if(newDefaultProperties == null)
                    if(isCloningNecessary)
                        newDefaultProperties = (HashMap) defaultProperties.clone();
                    else
                        newDefaultProperties = defaultProperties;

                HashMap oldPropertiesForPrefix = (HashMap) defaultProperties.get(prefixKey);
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

    protected HashMap computeDefaultProperties(JComponent component,
        HashMap defaultPropertiesForComponent, HashMap defaultProperties,
        java.util.List keys) {

        String name = component.getName();
        if(rootName.equals(name))
            return cloneIfNecessary(defaultPropertiesForComponent, (HashMap)
                defaultProperties.get(rootName), keys, false);
        else {

            JComponent parent = (JComponent) component.getParent();
            HashMap defaultPropertiesForParent = computeDefaultProperties(parent,
                defaultPropertiesForComponent, defaultProperties, keys);
            if(defaultProperties.isEmpty())
                return defaultPropertiesForParent;

            HashMap defaultPropertiesForChild = (HashMap) ((HashMap)
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
