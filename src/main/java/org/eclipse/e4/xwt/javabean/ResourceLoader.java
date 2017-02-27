/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.javabean;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IEventConstants;
import org.eclipse.e4.xwt.IIndexedElement;
import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.INamespaceHandler;
import org.eclipse.e4.xwt.IStyle;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.e4.xwt.Tracking;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.e4.xwt.XWTMaps;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.e4.xwt.callback.ICreatedCallback;
import org.eclipse.e4.xwt.callback.ILoadedCallback;
import org.eclipse.e4.xwt.core.IBinding;
import org.eclipse.e4.xwt.core.IDynamicBinding;
import org.eclipse.e4.xwt.core.IEventHandler;
import org.eclipse.e4.xwt.core.IRenderingContext;
import org.eclipse.e4.xwt.core.IVisualElementLoader;
import org.eclipse.e4.xwt.core.Setter;
import org.eclipse.e4.xwt.core.Style;
import org.eclipse.e4.xwt.core.TriggerBase;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.internal.core.Core;
import org.eclipse.e4.xwt.internal.core.DataBindingTrack;
import org.eclipse.e4.xwt.internal.core.ScopeKeeper;
import org.eclipse.e4.xwt.internal.utils.ClassLoaderUtil;
import org.eclipse.e4.xwt.internal.utils.DocumentObjectSorter;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.NamespaceHelper;
import org.eclipse.e4.xwt.internal.utils.ObjectUtil;
import org.eclipse.e4.xwt.internal.utils.TableEditorHelper;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.internal.xml.Attribute;
import org.eclipse.e4.xwt.internal.xml.DocumentObject;
import org.eclipse.e4.xwt.internal.xml.Element;
import org.eclipse.e4.xwt.javabean.metadata.properties.PropertiesConstants;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableItemProperty;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.utils.PathHelper;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class ResourceLoader implements IVisualElementLoader {
	static Map<String, Object> EMPTY_MAP = Collections.EMPTY_MAP;

	static final String RESOURCE_LOADER_PROPERTY = "XWT.ResourceLoader";

	private static final HashMap<String, Collection<Class<?>>> DELAYED_ATTRIBUTES = new HashMap<String, Collection<Class<?>>>();
	private static final String COLUMN = "Column";
	
	private Map<String, Object> options;

	protected ResourceLoader parentLoader;
	protected IRenderingContext context;
	protected IXWTLoader loader;

	protected Object scopedObject;
	protected ScopeKeeper nameScoped;
	protected LoadingData loadData = new LoadingData();

	class LoadingData {
		protected LoadingData parent;
		protected Object clr;
		protected Collection<IStyle> styles = Collections.EMPTY_LIST;
		private Object loadedObject = null;
		private Method loadedMethod = null;
		private Widget hostCLRWidget = null;
		private Object currentWidget = null;
		private Object host = null;
		private Object dataContext = null;

		public Object getDataContext() {
			return dataContext;
		}

		public void setDataContext(Object dataContext) {
			this.dataContext = dataContext;
		}

		public Object getHost() {
			return host;
		}

		public Object getCurrentWidget() {
			return currentWidget;
		}

		public void setCurrentWidget(Object currentWidget) {
			this.currentWidget = currentWidget;
		}

		public LoadingData getParent() {
			return parent;
		}

		public LoadingData() {
		}

		public LoadingData(LoadingData loadingData, Object host) {
			this.loadedObject = loadingData.loadedObject;
			this.loadedMethod = null;
			this.hostCLRWidget = loadingData.hostCLRWidget;
			this.parent = loadingData;
			this.styles = loadingData.styles;
			this.clr = loadingData.clr;
			this.currentWidget = loadingData.currentWidget;
			this.dataContext = loadingData.dataContext;
			this.host = host;
		}

		public Collection<IStyle> getStyles() {
			return styles;
		}

		public void setStyles(Collection<IStyle> styles) {
			this.styles = styles;
		}

		public Object getClr() {
			return clr;
		}

		public void setClr(Object clr) {
			this.clr = clr;
		}

		public void inject(Object targetObject, String name) {
			doInject(targetObject, name, null);
		}

		protected void doInject(Object targetObject, String name,
				Object previousClr) {
			Class<?> filedType = targetObject.getClass();
			if (clr != null && (previousClr != clr || previousClr == null)) {
				for (Field field : clr.getClass().getDeclaredFields()) {
					UI annotation = field.getAnnotation(UI.class);
					if (annotation != null) {
						if (!field.getType().isAssignableFrom(filedType)) {
							continue;
						}
						String annotationValue = annotation.value();
						if (annotationValue == null
								|| annotationValue.length() == 0) {
							if (field.getName().equals(name)) {
								field.setAccessible(true);
								try {
									field.set(clr, targetObject);
									return;
								} catch (Exception e) {
								}
							}
						} else if (annotationValue.equals(name)) {
							field.setAccessible(true);
							try {
								field.set(clr, targetObject);
								break;
							} catch (Exception e) {
							}
						}
					}
				}
			}
			if (parent != null) {
				parent.doInject(targetObject, name, clr);
			}
		}

		public void updateEvent(IRenderingContext context, Widget control,
				IEvent event, String handler) {
			IEventHandler eventController = UserData
					.updateEventController(control);
			Method method = null;
			Object clrObject = null;
			LoadingData current = this;
			ResourceLoader currentParentLoader = parentLoader;
			while (current != null) {
				Object receiver = current.getClr();
				if (receiver != null) {
					Class<?> clazz = receiver.getClass();
					method = ObjectUtil.findMethod(clazz, handler,
							control.getClass(), Event.class);
					if (method == null) {
						method = ObjectUtil.findMethod(clazz, handler,
								Event.class);
					}
					if (method == null) {
						// Load again.
						clazz = ClassLoaderUtil.loadClass(context
								.getLoadingContext(), clazz.getName());
						method = ObjectUtil.findMethod(clazz, handler,
								Event.class);
					}
					if (method != null) {
						clrObject = receiver;
						if (event.getName().equalsIgnoreCase(
								IEventConstants.XWT_LOADED)) {
							method.setAccessible(true);
							this.loadedObject = receiver;
							this.loadedMethod = method;
							this.hostCLRWidget = control;
						}
						eventController.setEvent(event, control, clrObject,
								control, method);
						break;
					}
				}
				current = current.getParent();
				if (current == null && currentParentLoader != null) {
					current = currentParentLoader.loadData;
					currentParentLoader = currentParentLoader.parentLoader;
				}
			}
			if (method == null) {
				LoggerManager.log(new XWTException("Event handler \"" + handler
						+ "\" is not found."));
			}
		}

		public void end() {
			if (parent == null || clr != parent.getClr()) {
				Method method = ObjectUtil.findDeclaredMethod(clr.getClass(),
						"initializeComponent");
				if (method == null) {
					method = ObjectUtil.findDeclaredMethod(clr.getClass(),
							"InitializeComponent");
				}
				if (method != null) {
					try {
						method.setAccessible(true);
						method.invoke(clr);
					} catch (Exception e) {
						LoggerManager.log(e);
					}
				}
			}
			// Try to invoke loaded event every time?
			if (loadedObject != null && loadedMethod != null
					&& hostCLRWidget != null) {
				Event event = new Event();
				event.doit = true;
				event.widget = hostCLRWidget;
				try {
					if (loadedMethod.getParameterTypes().length == 1) {
						loadedMethod.invoke(loadedObject,
								new Object[] { event });
					} else if (loadedMethod.getParameterTypes().length == 2) {
						loadedMethod.invoke(loadedObject, new Object[] {
								loadedObject, event });
					}
				} catch (IllegalArgumentException e1) {
					throw new XWTException("");
				} catch (IllegalAccessException e1) {
					throw new XWTException("");
				} catch (InvocationTargetException e1) {
					throw new XWTException("");
				}					
				loadedObject = null;
				loadedMethod = null;
				hostCLRWidget = null;
			}
		}

		public void addStyle(IStyle style) {
			if (styles == Collections.EMPTY_LIST) {
				styles = new ArrayList<IStyle>();
			}
			styles.add(style);
		}
	}

	private DataBindingTrack dataBindingTrack;

	/**
	 * @param context
	 */
	public ResourceLoader(IRenderingContext context, IXWTLoader loader) {
		this.context = context;
		this.loader = loader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IVisualElementLoader#createCLRElement(org.eclipse.
	 * e4.xwt.Element, org.eclipse.e4.xwt.ILoadData,
	 * org.eclipse.e4.xwt.IResourceDictionary)
	 */
	public Object createCLRElement(Element element, Map<String, Object> options) {
		try {
			this.options = options;
			Composite parent = (Composite) options
					.get(IXWTLoader.CONTAINER_PROPERTY);
			if (!loader.getTrackings().isEmpty()) {
				dataBindingTrack = new DataBindingTrack();
			}
			parentLoader = (ResourceLoader) options
					.get(RESOURCE_LOADER_PROPERTY);
			options.remove(RESOURCE_LOADER_PROPERTY);
			ResourceDictionary resourceDictionary = (ResourceDictionary) options
					.get(IXWTLoader.RESOURCE_DICTIONARY_PROPERTY);

			if (resourceDictionary != null) {
				Object styles = resourceDictionary.get(Core.DEFAULT_STYLES_KEY);
				if (styles != null) {
					loadData.setStyles((Collection<IStyle>) styles);
					resourceDictionary.remove(Core.DEFAULT_STYLES_KEY);
				}
			}

			Object control = doCreate(parent, element, null, options);

			// get databinding messages and print into console view
			if (dataBindingTrack != null) {
				String dataBindingMessage = dataBindingTrack
						.getDataBindMessage();// getDataBindMessage();
				org.eclipse.e4.xwt.ILogger log = loader.getLogger();
				log.addMessage(dataBindingMessage, Tracking.DATABINDING);
				log.printInfo(dataBindingMessage, Tracking.DATABINDING, loader
						.getTrackings());
			}
			if (control instanceof Composite) {
				((Composite) control).layout();
			}
			ILoadedCallback loadedAction = (ILoadedCallback) options
					.get(IXWTLoader.LOADED_CALLBACK);
			if (loadedAction != null) {
				loadedAction.onLoaded(control);
			}
			return control;
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw ((RuntimeException) e);
			}

			throw new XWTException(e);
		}
	}

	protected Object doCreate(Object parent, Element element, Class<?> constraintType,
			Map<String, Object> options) throws Exception {
		int styles = -1;
		if (options.containsKey(IXWTLoader.INIT_STYLE_PROPERTY)) {
			styles = (Integer) options.get(IXWTLoader.INIT_STYLE_PROPERTY);
		}

		ResourceDictionary dico = (ResourceDictionary) options
				.get(IXWTLoader.RESOURCE_DICTIONARY_PROPERTY);
		Object dataContext = options.get(IXWTLoader.DATACONTEXT_PROPERTY);
		Object bindingContext = options
				.get(IXWTLoader.BINDING_CONTEXT_PROPERTY);
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace)) {
			if (IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
				return null;
			}
			if (IConstants.XAML_X_TYPE.equalsIgnoreCase(name)
					&& constraintType != null && constraintType == Class.class) {
				DocumentObject[] children = element.getChildren();
				if (children != null && children.length > 0) {
					if (children[0] instanceof Element) {
						Element type = (Element) children[0];
						IMetaclass metaclass = loader.getMetaclass(type
								.getName(), type.getNamespace());
						if (metaclass != null) {
							return metaclass.getType();
						}
					}
				} else {
					String content = element.getContent();
					return loader.convertFrom(Class.class, content);
				}
			}
			return null;
		}
		IMetaclass metaclass = loader.getMetaclass(name, namespace);
		if (constraintType != null
				&& !(IBinding.class.isAssignableFrom(metaclass.getType()))
				&& (!constraintType.isAssignableFrom(metaclass.getType()))) {
			if (!constraintType.isArray()
					|| !constraintType.getComponentType().isAssignableFrom(
							metaclass.getType())) {
				return null;
			}
		}
		Object targetObject = null;
		Integer styleValue = getStyleValue(element, styles);

		if (parent == null || metaclass.getType() == Shell.class) {
			if (dataBindingTrack != null) {
				dataBindingTrack.addWidgetElement(element);
			}
			Shell shell = null;
			if (styleValue == null || styleValue == -1) {
				styleValue = SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX
						| SWT.RESIZE;
			}
			Display display = Display.getDefault();
			shell = new Shell(display, styleValue);
			targetObject = shell;
			invokeCreatededAction(element, targetObject);
			loadData.setCurrentWidget(shell);

			if (metaclass.getType() != Shell.class) {
				shell.setLayout(new FillLayout());
				return doCreate(targetObject, element, constraintType, options);
			} else {
				if (bindingContext != null) {
					setBindingContext(metaclass, targetObject, dico,
							bindingContext);
				}
				if (dataContext != null) {
					setDataContext(metaclass, targetObject, dico, dataContext);
				}
			}
			pushStack(parent);

			// for Shell
			Attribute classAttribute = element.getAttribute(
					IConstants.XWT_X_NAMESPACE, IConstants.XAML_X_CLASS);
			if (classAttribute != null) {
				String className = classAttribute.getContent();
				loadShellCLR(className, shell);
			}

		} else {
			pushStack(parent);

			//
			// load the content in case of UserControl
			//
			Class<?> type = metaclass.getType();
			URL file = type.getResource(type.getSimpleName()
					+ IConstants.XWT_EXTENSION_SUFFIX);
			if (file != null && nameScoped != null) {
				if (parent instanceof Composite) {
					Object childDataContext = getDataContext(element,
							(Widget) parent);
					Object childBindingContext = getBindingContext(element,
							(Widget) parent);
					if (dataContext != null) {
						childDataContext = dataContext;
					}
					if (bindingContext != null) {
						childBindingContext = bindingContext;
					}
					Map<String, Object> nestedOptions = new HashMap<String, Object>();
					nestedOptions.put(IXWTLoader.CONTAINER_PROPERTY, parent);
					if (styleValue != null) {
						nestedOptions.put(IXWTLoader.INIT_STYLE_PROPERTY,
								styleValue);
					}
					nestedOptions.put(IXWTLoader.DATACONTEXT_PROPERTY,
							childDataContext);
					nestedOptions.put(IXWTLoader.BINDING_CONTEXT_PROPERTY,
							childBindingContext);
					nestedOptions.put(RESOURCE_LOADER_PROPERTY, this);
					targetObject = loader.loadWithOptions(file, nestedOptions);
					if (targetObject == null) {
						return null;
					}
					invokeCreatededAction(element, targetObject);
				} else
					throw new XWTException(
							"Cannot add user control: Parent is not a composite");
			} else {
				Object[] parameters = null;
				if (TableViewerColumn.class.isAssignableFrom(type)) {
					int columnIndex = getColumnIndex(element);
					parameters = (styleValue != null ? new Object[] { parent,
							styleValue, columnIndex } : new Object[] { parent,
							SWT.NONE, columnIndex });
				} else {
					parameters = (styleValue != null ? new Object[] { parent,
							styleValue } : new Object[] { parent });
				}

				// x:Class
				{
					Attribute classAttribute = element
							.getAttribute(IConstants.XWT_X_NAMESPACE,
									IConstants.XAML_X_CLASS);
					if (classAttribute != null) {
						String className = classAttribute.getContent();
						targetObject = loadCLR(className, parameters, metaclass
								.getType(), options);
					} else {
						Object clr = options.get(XWTLoader.CLASS_PROPERTY);
						if (clr != null) {
							loadData.setClr(clr);
						}
					}
					if (targetObject == null) {
						targetObject = metaclass.newInstance(parameters);
						invokeCreatededAction(element, targetObject);
						Widget widget = UserData.getWidget(targetObject);
						if (widget != null) {
							Object clr = loadData.getClr();
							if (clr != null) {
								UserData.setCLR(widget, clr);
							}
						}
					} else {
						metaclass = loader.getMetaclass(targetObject);
					}
				}

				if (targetObject == null) {
					return null;
				}
			}
		}
		Widget widget = UserData.getWidget(targetObject);
		if (widget != null) {
			loadData.setCurrentWidget(targetObject);
		}
		if (scopedObject == null && widget != null) {
			scopedObject = widget;
			nameScoped = new ScopeKeeper((parent == null ? null : UserData
					.findScopeKeeper((Widget) parent)), widget);
			UserData.bindNameContext((Widget) widget, nameScoped);
		}

		// set first data context and resource dictionary
		setDataContext(metaclass, targetObject, dico, dataContext);
		if (bindingContext != null) {
			setBindingContext(metaclass, targetObject, dico, bindingContext);
		}

		applyStyles(element, targetObject);

		if (dataBindingTrack != null) {
			dataBindingTrack.tracking(targetObject, element, dataContext);
		}

		// set parent relationship and viewer
		if (targetObject instanceof Widget) {
			if (parent != null) {
				UserData.setParent(targetObject, parent);
			}
		} else if (JFacesHelper.isViewer(targetObject)) {
			UserData.setParent(targetObject, parent);
			UserData.setViewer(targetObject, targetObject);
		} else if (targetObject instanceof TableItemProperty.Cell) {
			((TableItemProperty.Cell) targetObject)
					.setParent((TableItem) parent);
		}

		for (Map.Entry<String, Object> entry : options.entrySet()) {
			String key = entry.getKey();
			if (IXWTLoader.CONTAINER_PROPERTY.equalsIgnoreCase(key)
					|| IXWTLoader.INIT_STYLE_PROPERTY.equalsIgnoreCase(key)
					|| IXWTLoader.DATACONTEXT_PROPERTY.equalsIgnoreCase(key)
					|| IXWTLoader.BINDING_CONTEXT_PROPERTY
							.equalsIgnoreCase(key)
					|| IXWTLoader.RESOURCE_DICTIONARY_PROPERTY
							.equalsIgnoreCase(key)
					|| IXWTLoader.CLASS_PROPERTY.equalsIgnoreCase(key)
					|| IXWTLoader.LOADED_CALLBACK.equalsIgnoreCase(key)
					|| IXWTLoader.CREATED_CALLBACK.equalsIgnoreCase(key)
					|| IXWTLoader.BEFORE_PARSING_CALLBACK.equalsIgnoreCase(key)
					|| IXWTLoader.DESIGN_MODE_PROPERTY.equalsIgnoreCase(key)) {
				continue;
			}
			IProperty property = metaclass.findProperty(key);
			if (property == null) {
				throw new XWTException("Property " + key + " not found.");
			}
			property.setValue(targetObject, entry.getValue());
		}

		List<String> delayedAttributes = new ArrayList<String>();
		init(metaclass, targetObject, element, delayedAttributes);
		if (targetObject instanceof Style && element.getChildren().length > 0) {
			Collection<Setter> setters = new ArrayList<Setter>();
			for (DocumentObject doc : element.getChildren()) {
				Object child = doCreate(targetObject, (Element) doc, null,
						Collections.EMPTY_MAP);
				if (!(child instanceof Setter)) {
					throw new XWTException("Setter is expected in Style.");
				}
				setters.add((Setter) child);
			}
			((Style) targetObject).setSetters(setters
					.toArray(new Setter[setters.size()]));
		} else if (targetObject instanceof ControlEditor) {
			for (DocumentObject doc : element.getChildren()) {
				Object editor = doCreate(parent, (Element) doc, null,
						Collections.EMPTY_MAP);
				if (editor != null && editor instanceof Control) {
					((ControlEditor) targetObject).setEditor((Control) editor);
					((Control) editor).setData(
							PropertiesConstants.DATA_CONTROLEDITOR_OF_CONTROL,
							targetObject);
				}
			}
		} else if (targetObject instanceof IDataProvider) {
			for (DocumentObject doc : element.getChildren()) {
				if (IConstants.XWT_X_NAMESPACE.equals(doc.getNamespace())) {
					String content = doc.getContent();
					if (content != null) {
						((IDataProvider) targetObject).setProperty(doc
								.getName(), content);
					}
				}
			}
		} else {
			for (DocumentObject doc : element.getChildren()) {
				doCreate(targetObject, (Element) doc, null,
						Collections.EMPTY_MAP);
			}
		}

		for (String delayed : delayedAttributes) {
			initAttribute(metaclass, targetObject, element, null, delayed);
		}

		postCreation(targetObject);
		popStack();
		return targetObject;
	}
	
	/**
	 * This method is invoked directly after creation of component instance, but
	 * before applying its attributes and creating children.
	 * 
	 * @param element
	 *            the source element in XML.
	 * @param targetObject
	 *            the created visual object.
	 */
	protected void postCreation0(Element element, Object targetObject) {
	}

	private void invokeCreatededAction(Element element, Object targetObject) {
		if (targetObject != null) {
			postCreation0(element, targetObject);			
		}
		if (options != null) {
			ICreatedCallback createdAction = (ICreatedCallback) options.get(IXWTLoader.CREATED_CALLBACK);
			if (createdAction != null) {
				createdAction.onCreated(targetObject);
			}
		}
	}
	
	/**
	 * This method is invoked after full creation of component, i.e. after
	 * creating its instance, applying its attributes and creating children.
	 */
	protected void postCreation(Object target) {
		Widget widget = UserData.getWidget(target);
		if (widget == null) {
			return;
		}
		TriggerBase[] triggers = UserData.getTriggers(widget);
		for (TriggerBase triggerBase : triggers) {
			if (triggerBase != null) {
				triggerBase.prepare(target);
			}
		}
		for (TriggerBase triggerBase : triggers) {
			if (triggerBase != null) {
				triggerBase.on(target);
			}
		}
	}

	protected void setDataContext(IMetaclass metaclass, Object targetObject,
			ResourceDictionary dico, Object dataContext)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {
		Object control = null;
		IMetaclass widgetMetaclass = metaclass;
		if (JFacesHelper.isViewer(targetObject)) {
			Widget widget = JFacesHelper.getControl(targetObject);
			widgetMetaclass = loader.getMetaclass(widget.getClass());
			control = targetObject;
		} else if (targetObject instanceof Widget) {
			control = (Widget) targetObject;
		} else {
			control = loadData.getCurrentWidget();
		}
		if (control != null) {
			if (targetObject instanceof IDynamicBinding) {
				IDynamicBinding dynamicBinding = (IDynamicBinding) targetObject;
				dynamicBinding.setControl(control);
				dynamicBinding.setHost(loadData.getHost());
			}
			if (dico != null) {
				UserData.setResources(control, dico);
			}
			if (dataContext != null) {
				IProperty property = widgetMetaclass
						.findProperty(IConstants.XAML_DATA_CONTEXT);
				if (property != null) {
					property.setValue(UserData.getWidget(control), dataContext);
				} else {
					throw new XWTException("DataContext is missing in "
							+ widgetMetaclass.getType().getName());
				}
			}
		}
	}

	protected void setBindingContext(IMetaclass metaclass, Object targetObject,
			ResourceDictionary dico, Object bindingContext)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {
		Object control = null;
		IMetaclass widgetMetaclass = metaclass;
		if (JFacesHelper.isViewer(targetObject)) {
			Widget widget = JFacesHelper.getControl(targetObject);
			widgetMetaclass = loader.getMetaclass(widget.getClass());
			control = targetObject;
		} else if (targetObject instanceof Widget) {
			control = (Widget) targetObject;
		} else {
			control = loadData.getCurrentWidget();
		}
		if (control != null) {
			if (targetObject instanceof IDynamicBinding) {
				IDynamicBinding dynamicBinding = (IDynamicBinding) targetObject;
				dynamicBinding.setControl(control);
				dynamicBinding.setHost(loadData.getHost());
			}
			if (dico != null) {
				UserData.setResources(control, dico);
			}
			if (bindingContext != null) {
				IProperty property = widgetMetaclass
						.findProperty(IConstants.XAML_BINDING_CONTEXT);
				if (property != null) {
					property.setValue(UserData.getWidget(control),
							bindingContext);
				} else {
					throw new XWTException("DataContext is missing in "
							+ widgetMetaclass.getType().getName());
				}
			}
		}
	}

	protected void applyStyles(Element element, Object targetObject) throws Exception {
		if (targetObject instanceof Widget) {
			Widget widget = (Widget) targetObject;
			Map<String, Object> dico = UserData.getLocalResources(widget);
			Attribute attribute = element
					.getAttribute(IConstants.XAML_RESOURCES);
			if (attribute == null) {
				attribute = element.getAttribute(IConstants.XWT_NAMESPACE,
						IConstants.XAML_RESOURCES);
			}
			if (attribute != null) {
				if (attribute.getChildren().length > 0) {
					if (dico == null) {
						dico = new ResourceDictionary();
						UserData.setResources(widget, dico);
					}

					for (DocumentObject doc : attribute.getChildren()) {
						Element elem = (Element) doc;
						Object doCreate = doCreate(widget, elem, null,
								EMPTY_MAP);
						Attribute keyAttribute = elem.getAttribute(
								IConstants.XWT_X_NAMESPACE,
								IConstants.XAML_X_KEY);
						if (keyAttribute == null) {
							keyAttribute = elem.getAttribute(
									IConstants.XWT_X_NAMESPACE,
									IConstants.XAML_X_TYPE);
						}
						if (keyAttribute != null) {
							dico.put(keyAttribute.getContent(), doCreate);
						}
						if (doCreate instanceof IStyle) {
							IStyle style = (IStyle) doCreate;
							loadData.addStyle(style);
						}
					}
				}
			}

			// apply the styles defined in parent's resources via TargetType
			Widget current = widget;
			while (current != null) {
				dico = UserData.getLocalResources(current);
				if (dico != null) {
					for (Object value : dico.values()) {
						if (value instanceof Style) {
							Style style = (Style) value;
							Class<?> targetType = style.getTargetType();
							if (targetType != null
									&& targetType.isInstance(widget)) {
								style.apply(targetObject);
							}
						}
					}
				}
				current = UserData.getParent(current);
			}
		}

		for (IStyle style : loadData.getStyles()) {
			style.applyStyle(targetObject);
		}
	}

	protected int getColumnIndex(Element columnElement) {
		String name = columnElement.getName();
		String namespace = columnElement.getNamespace();
		IMetaclass metaclass = loader.getMetaclass(name, namespace);
		int index = -1;
		Class<?> type = metaclass.getType();
		if (TableViewerColumn.class.isAssignableFrom(type)) {
			DocumentObject parent = columnElement.getParent();
			List<DocumentObject> children = DocumentObjectSorter.sortWithAttr(
					parent.getChildren(), "Index");
			index = children.indexOf(columnElement);
		}

		return index;
	}

	/**
	 * @param tableItem
	 */
	protected void installTableEditors(TableItem tableItem) {
		Table table = tableItem.getParent();
		TableColumn[] columns = table.getColumns();
		if (columns == null || columns.length == 0) {
			return;
		}
		for (TableColumn tableColumn : columns) {
			Object data = tableColumn
					.getData(PropertiesConstants.DATA_DEFINED_EDITOR);
			if (data == null || !(data instanceof Element)) {
				continue;
			}
			int column = table.indexOf(tableColumn);
			Element editor = (Element) data;
			try {
				TableEditor tableEditor = (TableEditor) doCreate(table, editor,
						null, EMPTY_MAP);
				if (tableEditor != null) {
					tableEditor.setColumn(column);
					tableEditor.setItem(tableItem);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	protected Object getDataContext(Element element, Widget swtObject) {
		// x:DataContext
		try {
			Object dataContext = loadData.getDataContext();
			if (dataContext != null) {
				return dataContext;
			}
			{
				Attribute dataContextAttribute = element
						.getAttribute(IConstants.XAML_DATA_CONTEXT);
				if (dataContextAttribute != null) {
					Widget composite = (Widget) swtObject;
					DocumentObject documentObject = dataContextAttribute
							.getChildren()[0];
					if (IConstants.XAML_STATICRESOURCES.equals(documentObject
							.getName())
							|| IConstants.XAML_DYNAMICRESOURCES
									.equals(documentObject.getName())) {
						String key = documentObject.getContent();
						dataContext = new StaticResourceBinding(composite, key);
						loadData.setDataContext(dataContext);
						return dataContext;
					} else if (IConstants.XAML_BINDING.equals(documentObject
							.getName())) {
						dataContext = doCreate(swtObject, (Element) documentObject,
								null, EMPTY_MAP);
						loadData.setDataContext(dataContext);
						return dataContext;
					} else {
						LoggerManager.log(new UnsupportedOperationException(
								documentObject.getName()));
					}
				}
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}

		return null;
	}

	protected Object getBindingContext(Element element, Widget swtObject) {
		// x:DataContext
		try {
			{
				Attribute dataContextAttribute = element
						.getAttribute(IConstants.XAML_BINDING_CONTEXT);
				if (dataContextAttribute != null) {
					Widget composite = (Widget) swtObject;
					DocumentObject documentObject = dataContextAttribute
							.getChildren()[0];
					if (IConstants.XAML_STATICRESOURCES.equals(documentObject
							.getName())
							|| IConstants.XAML_DYNAMICRESOURCES
									.equals(documentObject.getName())) {
						String key = documentObject.getContent();
						return new StaticResourceBinding(composite, key);
					} else if (IConstants.XAML_BINDING.equals(documentObject
							.getName())) {
						return doCreate(swtObject, (Element) documentObject,
								null, EMPTY_MAP);
					} else {
						LoggerManager.log(new UnsupportedOperationException(
								documentObject.getName()));
					}
				}
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}

		return null;
	}

	protected void pushStack(Object host) {
		loadData = new LoadingData(loadData, host);
	}

	protected void popStack() {
		LoadingData previous = loadData;
		loadData = previous.getParent();

		previous.end();
	}

	protected Integer getStyleValue(Element element, int styles) {
		Attribute attribute = element.getAttribute(IConstants.XWT_X_NAMESPACE,
				IConstants.XAML_STYLE);
		if (attribute == null) {
			if (styles != -1) {
				return styles;
			}
			return null;
		}
		if (styles == -1) {
			return (Integer) loader.findConvertor(String.class, Integer.class)
					.convert(attribute.getContent());
		}
		return styles
				| (Integer) loader.findConvertor(String.class, Integer.class)
						.convert(attribute.getContent());
	}

	protected void init(IMetaclass metaclass, Object targetObject, Element element,
			List<String> delayedAttributes) throws Exception {
		// editors for TableItem,
		if (targetObject instanceof TableItem) {
			installTableEditors((TableItem) targetObject);
		}

		// x:DataContext
		if (loadData.getDataContext() == null) {
			Attribute dataContextAttribute = element
					.getAttribute(IConstants.XAML_DATA_CONTEXT);
			if (dataContextAttribute != null) {
				IProperty property = metaclass
						.findProperty(IConstants.XAML_DATA_CONTEXT);
				Widget composite = (Widget) UserData.getWidget(targetObject);
				DocumentObject documentObject = dataContextAttribute
						.getChildren()[0];
				if (IConstants.XAML_STATICRESOURCES.equals(documentObject
						.getName())
						|| IConstants.XAML_DYNAMICRESOURCES
								.equals(documentObject.getName())) {
					String key = documentObject.getContent();
					property.setValue(composite, new StaticResourceBinding(
							composite, key));
				} else if (IConstants.XAML_BINDING.equals(documentObject
						.getName())) {
					Object object = doCreate(targetObject,
							(Element) documentObject, null, EMPTY_MAP);
					property.setValue(composite, object);
				} else {
					LoggerManager.log(new UnsupportedOperationException(
							documentObject.getName()));
				}
			}
		}

		HashSet<String> done = new HashSet<String>();

		Attribute nameAttr = element.getAttribute(IConstants.XAML_X_NAME);
		if (nameAttr == null) {
			nameAttr = element.getAttribute(IConstants.XWT_X_NAMESPACE,
					IConstants.XAML_X_NAME);
		}
		if (nameAttr != null && UserData.getWidget(targetObject) != null) {
			String value = nameAttr.getContent();
			loadData.inject(targetObject, value);

			nameScoped.addNamedObject(value, targetObject);
			done.add(IConstants.XAML_X_NAME);
		}

		for (String attrName : element.attributeNames()) {
			if (IConstants.XWT_X_NAMESPACE.equals(element
					.getAttribute(attrName).getNamespace())) {
				continue;
			} else if (delayedAttributes != null
					&& isDelayedProperty(attrName.toLowerCase(), metaclass
							.getType()))
				delayedAttributes.add(attrName);
			else {
				if (!done.contains(attrName)) {
					initAttribute(metaclass, targetObject, element, null,
							attrName);
					done.add(attrName);
				}
			}
		}

		for (String namespace : element.attributeNamespaces()) {
			if (IConstants.XWT_X_NAMESPACE.equals(namespace)) {
				for (String attrName : element.attributeNames(namespace)) {
					if ("class".equalsIgnoreCase(attrName)
							|| IConstants.XAML_STYLE.equalsIgnoreCase(attrName)) {
						continue; // done before
					} else if (IConstants.XAML_X_NAME
							.equalsIgnoreCase(attrName)) {
						nameScoped
								.addNamedObject(element.getAttribute(namespace,
										attrName).getContent(), targetObject);
						done.add(attrName);
					} else if (IConstants.XAML_DATA_CONTEXT
							.equalsIgnoreCase(attrName)) {
						continue; // done before
					} else if (IConstants.XAML_X_ARRAY
							.equalsIgnoreCase(attrName)) {
						IProperty property = metaclass.findProperty(attrName);
						Class<?> type = property.getType();
						Object value = getArrayProperty(type, targetObject,
								element, attrName);
						if (value != null) {
							property.setValue(targetObject, value);
						}
					} else if (IConstants.XAML_RESOURCES
							.equalsIgnoreCase(attrName)) {
						continue;
					} else {
						if (!done.contains(attrName)) {
							initAttribute(metaclass, targetObject, element,
									namespace, attrName);
							done.add(attrName);
						}
					}
				}
				continue;
			}

			for (String attrName : element.attributeNames(namespace)) {
				if (IConstants.XAML_X_NAME.equalsIgnoreCase(attrName)
						&& (targetObject instanceof Widget)) {
					continue;
				}
				if (!done.contains(attrName)) {
					initAttribute(metaclass, targetObject, element, namespace,
							attrName);
					done.add(attrName);
				}
			}
		}
		for (String attrName : element.attributeNames()) {
			if (IConstants.XAML_X_NAME.equalsIgnoreCase(attrName)
					&& UserData.getWidget(targetObject) != null) {
				continue;
			}
			if (!done.contains(attrName)
					&& !delayedAttributes.contains(attrName)) {
				initAttribute(metaclass, targetObject, element, null, attrName);
				done.add(attrName);
			}
		}

		//
		// handle foreigner namespace
		//
		for (String namespace : element.attributeNamespaces()) {
			if (XWT.isXWTNamespace(namespace)) {
				continue;
			}
			INamespaceHandler namespaceHandler = loader
					.getNamespaceHandler(namespace);
			if (namespaceHandler != null) {
				for (String attrName : element.attributeNames(namespace)) {
					Attribute attribute = element.getAttribute(namespace,
							attrName);
					Widget widget = UserData.getWidget(loadData
							.getCurrentWidget());
					namespaceHandler.handleAttribute(widget, targetObject,
							attrName, attribute.getContent());
				}
			}
		}
	}

	protected Object getArrayProperty(Class<?> type, Object swtObject,
			DocumentObject element, String attrName)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {
		if (!type.isArray()) {
			throw new XWTException("Type mismatch: property " + attrName
					+ " isn't an array.");
		}

		Class<?> arrayType = type.getComponentType();
		if (arrayType != null) {
			List<Object> list = new ArrayList<Object>();
			for (DocumentObject childModel : element.getChildren()) {
				if (!(childModel instanceof Element)) {
					continue;
				}
				Object child = createInstance(swtObject, (Element) childModel);
				list.add(child);
			}
			Object[] array = (Object[]) Array.newInstance(arrayType, list
					.size());
			list.toArray(array);

			for (int i = 0; i < array.length; i++) {
				if (array[i] instanceof IIndexedElement) {
					((IIndexedElement) array[i]).setIndex(swtObject, i);
				}
			}
			return array;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Object getCollectionProperty(Class<?> type, Object swtObject,
			DocumentObject element, String attrName)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {
		Collection<Object> collector = null;
		if (type.isInterface()) {
			collector = new ArrayList<Object>();
		} else {
			if (Modifier.isAbstract(type.getModifiers())) {
				LoggerManager.log(new XWTException("Collection "
						+ type.getSimpleName() + " is abstract type"));
			}
			try {
				collector = (Collection) type.newInstance();
			} catch (InstantiationException e) {
				LoggerManager.log(new XWTException(e));
			}
		}

		for (DocumentObject childModel : element.getChildren()) {
			if (!(childModel instanceof Element)) {
				continue;
			}
			Object child = createInstance(swtObject, (Element) childModel);
			collector.add(child);
			if (child instanceof IIndexedElement) {
				((IIndexedElement) child).setIndex(swtObject,
						collector.size() - 1);
			}
		}
		return collector;
	}

	protected String findNamespace(DocumentObject context, String prefix) {
		while (context != null && !(context instanceof Element)) {
			context = context.getParent();
		}
		if (context == null) {
			return null;
		}
		Element element = (Element) context;

		if (prefix != null) {
			prefix = (prefix.length() == 0 ? null : prefix);
		}

		String namespace = element.getXmlns(prefix);
		if (namespace != null) {
			return namespace;
		}
		DocumentObject parent = element.getParent();
		return findNamespace(parent, prefix);
	}

	protected Object createInstance(Object swtObject, Element element) {
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace)
				&& IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
			return null;
		}
		try {
			Class<?> type = NamespaceHelper.loadCLRClass(context
					.getLoadingContext(), name, namespace);
			IMetaclass metaclass = loader.getMetaclass(name, namespace);
			if (type == null) {
				if (metaclass != null)
					type = metaclass.getType();
			}
			if (metaclass == null) {
				throw new XWTException("Class for " + name + " is not found.");
			}
			// type = expected type;
			// Need to support the
			String content = element.getContent();
			Object instance = null;
			if (content == null) {
				instance = metaclass.newInstance(new Object[] { swtObject });
				invokeCreatededAction(element, instance);
				if (instance instanceof TableEditor) {
					// TODO should be moved into IMetaclass
					TableEditor tableEditor = (TableEditor) instance;
					if (swtObject instanceof TableItem) {
						TableItem item = (TableItem) swtObject;
						tableEditor.setItem(item);
						for (DocumentObject doc : element.getChildren()) {
							Control control = (Control) doCreate(
									((TableItem) swtObject).getParent(),
									(Element) doc, null, EMPTY_MAP);
							tableEditor.setEditor(control);
							int column = getColumnValue(element);
							TableEditorHelper.initEditor(item, control, column);
						}
					}
				}
			} else {
				Constructor<?> constructor = type.getConstructor(type);
				if (constructor != null) {
					instance = constructor.newInstance(loader.convertFrom(type,
							content));
					invokeCreatededAction(element, instance);
				} else {
					LoggerManager.log(new XWTException("Constructor \"" + name
							+ "(" + type.getSimpleName() + ")\" is not found"));
				}
			}
			List<String> delayedAttributes = new ArrayList<String>();
			init(metaclass, instance, element, delayedAttributes);
			for (String delayed : delayedAttributes) {
				initAttribute(metaclass, instance, element, null, delayed);
			}

			for (DocumentObject doc : element.getChildren()) {
				doCreate(instance, (Element) doc, null, Collections.EMPTY_MAP);
			}
			return instance;
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	static protected int getColumnValue(Element context) {
		Attribute attribute = context.getAttribute(COLUMN);
		if (attribute != null) {
			String content = attribute.getContent();
			if (content != null) {
				return Integer.parseInt(content);
			}
		}
		return 0;
	}

	protected void loadShellCLR(String className, Shell shell) {
		Class<?> type = ClassLoaderUtil.loadClass(context.getLoadingContext(),
				className);
		try {
			Object instance = type.newInstance();
			loadData.setClr(instance);
			UserData.setCLR(shell, instance);
		} catch (Exception e) {
			LoggerManager.log(e);
		}
	}

	protected Object loadCLR(String className, Object[] parameters,
			Class<?> currentTagType, Map<String, Object> options) {
		Class<?> type = ClassLoaderUtil.loadClass(context.getLoadingContext(),
				className);
		if (type == null) {
			return null;
		}
		try {
			Object clr = options.get(XWTLoader.CLASS_PROPERTY);
			if (clr != null && type.isInstance(clr)) {
				loadData.setClr(clr);
				if (clr instanceof Widget) {
					UserData.setCLR((Widget) clr, clr);
				}
			} else if (currentTagType != null
					&& currentTagType.isAssignableFrom(type)) {
				IMetaclass metaclass = loader.getMetaclass(type);
				Object instance = metaclass.newInstance(parameters);
				loadData.setClr(instance);
				// use x:Class's instance
				if (instance instanceof Widget) {
					UserData.setCLR((Widget) instance, instance);
				}
				return instance;
			} else {
				Object instance = type.newInstance();
				loadData.setClr(instance);
				if (instance instanceof Widget) {
					UserData.setCLR((Widget) instance, instance);
				}
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	protected void initAttribute(IMetaclass metaclass, Object targetObject,
			Element element, String namespace, String attrName)
			throws Exception {
		if (attrName.indexOf('.') != -1) {
			String[] segments = attrName.split("\\.");
			IMetaclass currentMetaclass = metaclass;
			Object target = targetObject;
			for (int i = 0; i < segments.length - 1; i++) {
				IProperty property = currentMetaclass.findProperty(segments[i]);
				if (property != null) {
					target = property.getValue(target);
					if (target == null) {
						LoggerManager.log(new XWTException("Property \""
								+ segments[i] + "\" is null."));
					}
					currentMetaclass = loader.getMetaclass(target);
				} else {
					LoggerManager.log(new XWTException("Property \""
							+ segments[i] + "\" not found in "
							+ element.getName() + "."));
				}
			}
			initSegmentAttribute(currentMetaclass,
					segments[segments.length - 1], target, element, namespace,
					attrName);
			return;
		}
		initSegmentAttribute(metaclass, attrName, targetObject, element,
				namespace, attrName);
	}

	protected void addCommandExecuteListener(String commandName, final Widget targetButton) {
		final ICommand commandObj = loader.getCommand(commandName);
		if (commandObj != null) {
			targetButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					commandObj.execute(targetButton);
				}
			});
		}
	}

	protected void initSegmentAttribute(IMetaclass metaclass, String propertyName,
			Object target, Element element, String namespace, String attrName)
			throws Exception {
		Attribute attribute = namespace == null ? element
				.getAttribute(attrName) : element.getAttribute(namespace,
				attrName);
		if (attribute == null) {
			attribute = element.getAttribute(attrName);
		}
		IProperty property = null;
		boolean isAttached = false;
		{
			String namePrefix = attribute.getNamePrefix();
			if (namePrefix == null) {
				property = metaclass.findProperty(propertyName);
			} else {
				//
				IMetaclass metaclassAttached = loader.getMetaclass(namePrefix,
						attribute.getNamespace());
				if (metaclassAttached != null) {
					property = metaclassAttached.findProperty(propertyName);
					isAttached = true;
				} else {
					LoggerManager.log(attribute.getNamespace() + " -> "
							+ namePrefix + " is not found.");
					return;
				}
			}
		}

		if (propertyName.equals(IConstants.XAML_DATA_CONTEXT)) {
			property = null;
		}
		if (IConstants.XAML_COMMAND.equalsIgnoreCase(propertyName)
				&& ICommand.class.isAssignableFrom(property.getType())
				&& (target instanceof Widget)) {
			addCommandExecuteListener(attribute.getContent(), (Widget) target);
		}
		if (property == null) {
			if (options.get(IXWTLoader.DESIGN_MODE_PROPERTY) == Boolean.TRUE) {
				return;
			}
			// prepare event
			IEvent event = metaclass.findEvent(attrName);
			if (event == null) {
				return;
			}
			// add events for controls and items.
			if (!(target instanceof Widget)) {
				return;
			}
			loadData.updateEvent(context, (Widget) target, event, attribute
					.getContent());
			return;
		}

		String contentValue = attribute.getContent();
		if ("MenuItem".equalsIgnoreCase(element.getName())
				&& "Text".equalsIgnoreCase(attrName)) {
			Attribute attributeAccelerator = element
					.getAttribute("Accelerator");
			if (attributeAccelerator != null) {
				contentValue = contentValue + '\t'
						+ getContentValue(attributeAccelerator.getContent());
			}
		}

		if (contentValue != null && "Accelerator".equalsIgnoreCase(attrName)) {
			contentValue = XWTMaps.getCombAccelerator(contentValue);
			if (contentValue.contains("'")) {
				contentValue = removeSubString(contentValue, "'");
			}
		}
		if (contentValue != null
				&& (Image.class.isAssignableFrom(property.getType()))) {
			contentValue = getImagePath(attribute, contentValue);
		}
		if (contentValue != null
				&& (URL.class.isAssignableFrom(property.getType()))) {
			contentValue = getSourceURL(contentValue);
		}
		Object value = null;
		DocumentObject[] children = attribute.getChildren();
		boolean usingExistingValue = false;
		if (contentValue == null) {
			Class<?> type = property.getType();
			if (Collection.class.isAssignableFrom(type)) {
				value = getCollectionProperty(type, target, attribute, attrName);
			} else {
				Object directTarget = null;
				if (TableViewerColumn.class.isAssignableFrom(type)
						&& attrName.equalsIgnoreCase("columns")) {
					children = DocumentObjectSorter.sortWithAttr(children,
							"Index").toArray(
							new DocumentObject[children.length]);
				} else {
					try {
						Object propertyValue = property.getValue(target);
						if (UserData.getWidget(propertyValue) != null) {
							directTarget = propertyValue;
							// use the existing property value as parent,
							// not need to add the constraint
							if (!property.isValueAsParent()) {
								type = null;
								usingExistingValue = true;
							}
						}
					} catch (Exception e) {
					}
				}
				if (directTarget == null) {
					directTarget = target;
				}

				for (DocumentObject child : children) {
					String name = child.getName();
					String ns = child.getNamespace();
					if (name.equalsIgnoreCase(IConstants.XAML_X_STATIC)
							&& ns.equals(IConstants.XWT_X_NAMESPACE)) {
						value = getStaticValue(child);
					} else if (name
							.equalsIgnoreCase(IConstants.XAML_STATICRESOURCES)
							&& ns.equals(IConstants.XWT_NAMESPACE)) {
						String key = child.getContent();
						value = new StaticResourceBinding(loadData
								.getCurrentWidget(), key);
					} else if ((IConstants.XWT_X_NAMESPACE.equals(ns) && IConstants.XAML_X_ARRAY
							.equalsIgnoreCase(name))) {
						value = getArrayProperty(property.getType(),
								directTarget, child, name);
					} else if (property.getType().isArray()) {
						value = getArrayProperty(property.getType(),
								directTarget, attribute, name);
						break;
					} else if (isAssignableFrom(element, TableColumn.class)
							&& isAssignableFrom(child, TableEditor.class)) {
						value = child;
					} else if (TableViewerColumn.class
							.isAssignableFrom(property.getType())
							&& attribute.getContent() != null) {
						value = attribute.getContent();
					} else {
						if ("Null".equals(child.getName()) && IConstants.XWT_X_NAMESPACE.equals(child.getNamespace())) {
							property.setValue(directTarget, null);
							return;
						} else {
							value = doCreate(directTarget, (Element) child, type,
									EMPTY_MAP);
							if (value == null
									&& type != null
									&& !(type == Table.class
											&& "TableColumn"
													.equals(child.getName()) && Table.class
											.isInstance(directTarget))) {
								throw new XWTException(child.getName()
										+ " cannot be a content of "
										+ type.getName() + " "
										+ target.getClass().getName() + "."
										+ property.getName());
							}
							if (value instanceof IDynamicBinding) {
								((IDynamicBinding) value).setType(attrName);
							}
						}
					}
				}
			}
		}
		if (contentValue != null && value == null
				&& !IConstants.XAML_COMMAND.equalsIgnoreCase(propertyName)) {
			if (property.getType().isInstance(Class.class)) {
				int index = contentValue.lastIndexOf(':');
				if (index != -1) {
					String prefix = contentValue.substring(0, index);
					contentValue = findNamespace(attribute, prefix)
							+ contentValue.substring(index);
				}
			}
			value = loader.convertFrom(property.getType(), contentValue);
		}
		if (!usingExistingValue) {
			if (value != null) {
				Class<?> propertyType = property.getType();
				if (!propertyType.isAssignableFrom(value.getClass())
						|| (value instanceof IBinding && !(IBinding.class
								.isAssignableFrom(propertyType)))) {
					Object orginalValue = value;
					IConverter converter = loader.findConvertor(value
							.getClass(), propertyType);
					if (converter != null) {
						value = converter.convert(value);
						if (value != null
								&& orginalValue instanceof IBinding
								&& !propertyType.isAssignableFrom(value
										.getClass())) {
							converter = loader.findConvertor(value.getClass(),
									propertyType);
							if (converter != null) {
								value = converter.convert(value);
							} else {
								LoggerManager.log(new XWTException("Convertor "
										+ value.getClass().getSimpleName()
										+ "->" + propertyType.getSimpleName()
										+ " is not found"));
							}
						}
					} else {
						LoggerManager.log(new XWTException("Convertor "
								+ value.getClass().getSimpleName() + "->"
								+ propertyType.getSimpleName()
								+ " is not found"));
					}
				}
				if (isAttached) {
					UserData.setLocalData(target, property, value);
				} else {
					property.setValue(target, value);
				}
			} else {
				if (value == null) {
					value = property.getValue(target);
				}
				if (value != null) {
					// create children.
					for (DocumentObject child : children) {
						String name = child.getName();
						String ns = child.getNamespace();
						if (!IConstants.XWT_X_NAMESPACE.equals(ns)
								|| !IConstants.XAML_X_ARRAY
										.equalsIgnoreCase(name)) {
							Class<?> type = property.getType();
							if (!Collection.class.isAssignableFrom(type)) {
								doCreate(value, (Element) child, null,
										EMPTY_MAP);
							}
						}
					}
				}
			}
		}

		if (attribute.attributeNames(IConstants.XWT_NAMESPACE).length > 0) {
			IMetaclass propertyMetaclass = loader.getMetaclass(property
					.getType());
			if (value == null) {
				value = property.getValue(target);
			}
			if (value != null) {
				List<String> delayedAttributes = new ArrayList<String>();
				init(propertyMetaclass, value, attribute, delayedAttributes);
				for (String delayed : delayedAttributes) {
					initAttribute(metaclass, target, element, null, delayed);
				}
			}
		}
	}

	/**
	 * @param contentValue
	 * @return
	 */
	protected String getSourceURL(String contentValue) {
		URL url = null;
		try {
			url = new URL(contentValue);
		} catch (MalformedURLException e) {
			if (!contentValue.startsWith("/")) {
				contentValue = "/" + contentValue;
			}
			ILoadingContext loadingContext = context.getLoadingContext();
			URL resource = loadingContext.getResource(contentValue);
			if (resource == null) {
				try {
					resource = new URL(context.getResourcePath() + contentValue);
					return resource.toString();
				} catch (MalformedURLException e1) {
				}
			}
			else {
				return resource.toString();
			}
		}
		if (url != null) {
			return url.toString();
		}
		return contentValue;
	}

	protected Class<?> getJavaType(DocumentObject element) {
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace)
				&& IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
			return null;
		}
		IMetaclass metaclass = loader.getMetaclass(name, namespace);
		if (metaclass == null) {
			return null;
		}
		return metaclass.getType();
	}

	protected boolean isAssignableFrom(DocumentObject element, Class<?> type) {
		Class<?> targetType = getJavaType(element);
		if (targetType == null) {
			return false;
		}
		return targetType.isAssignableFrom(type);
	}

	protected Object getStaticValue(DocumentObject child) {
		DocumentObject[] children = child.getChildren();
		if (children.length == 1) {
			Element element = (Element) children[0];
			if (element != null) {
				return ClassLoaderUtil.loadStaticMember(context
						.getLoadingContext(), element);
			}
		}
		return null;
	}

	protected String getImagePath(Attribute attribute, String contentValue) {
		String value = contentValue;
		try {
			File file = new File(contentValue);
			if (file.exists()) {
				return file.toURI().toURL().toString();
			}
			if (!contentValue.startsWith("/")) {
				URL url = context.getResourcePath();
				if (url != null) {
					return url.toString() + "/" + contentValue;
				}
				contentValue = "/" + contentValue;
			}
			ILoadingContext loadingContext = context.getLoadingContext();
			URL resource = loadingContext.getResource(contentValue);
			if (resource == null) {
				URL resourcePath = context.getResourcePath();
				String fPath = resourcePath.toString();
				String absolutePath = PathHelper.getAbsolutePath(fPath,
						contentValue);
				if ((file = new File(absolutePath)).exists()) {
					return file.toURI().toURL().toString();
				}
				resource = new URL(absolutePath);
			}
			return resource.toString();
		} catch (MalformedURLException e) {
			return value;
		}
	}

	protected String removeSubString(String str, String subString) {
		StringBuffer stringBuffer = new StringBuffer();
		int lenOfsource = str.length();
		int i;
		int posStart;
		for (posStart = 0; (i = str.indexOf(subString, posStart)) >= 0; posStart = i
				+ subString.length()) {
			stringBuffer.append(str.substring(posStart, i));
		}
		if (posStart < lenOfsource) {
			stringBuffer.append(str.substring(posStart));
		}
		return stringBuffer.toString();
	}

	protected String getContentValue(String text) {
		StringBuffer stringBuffer = new StringBuffer();
		String subString = "SWT.";
		String str = XWTMaps.getCombAccelerator(text);

		if (str.contains(subString)) {
			str = removeSubString(str, subString);
		}
		if (str.contains("'")) {
			str = removeSubString(str, "'");
		}
		if (str.contains(" ")) {
			str = removeSubString(str, " ");
		}
		if (str.contains("|")) {
			str = str.replace('|', '+');
		}
		stringBuffer.append(str);
		return stringBuffer.toString();

	}

	static protected boolean isDelayedProperty(String attr, Class<?> type) {
		Collection<Class<?>> types = DELAYED_ATTRIBUTES.get(attr);
		if (types == null) {
			return false;
		}
		if (types.contains(type)) {
			return true;
		}
		for (Class<?> class1 : types) {
			if (class1.isAssignableFrom(type)) {
				return true;
			}
		}

		return false;
	}

	static {
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(Viewer.class);
			DELAYED_ATTRIBUTES.put("input", types);
		}
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(Sash.class);
			types.add(SashForm.class);
			DELAYED_ATTRIBUTES.put("weights", types);
		}
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(Combo.class);
			types.add(CCombo.class);
			DELAYED_ATTRIBUTES.put("text", types);
		}
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(Browser.class);
			DELAYED_ATTRIBUTES.put("url", types);
		}
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(TableEditor.class);
			DELAYED_ATTRIBUTES.put("dynamic", types);
		}
	}

}
