package com.adobe.dp.css;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class CSSStylesheet {
	Vector statements = new Vector();

	Hashtable rulesBySelector = new Hashtable();

	public void add(Object rule) {
		if (rule instanceof SelectorRule) {
			SelectorRule srule = (SelectorRule) rule;
			Selector[] selectors = srule.selectors;
			if (selectors.length == 1)
				rulesBySelector.put(selectors[0], rule);
		}
		statements.add(rule);
	}

	public Selector getSimpleSelector(String elementName, String className) {
		NamedElementSelector elementSelector = null;
		if (elementName != null)
			elementSelector = new NamedElementSelector(null, null, elementName);
		if (className == null)
			return elementSelector;
		Selector selector = new ClassSelector(className);
		if (elementSelector != null)
			selector = new AndSelector(elementSelector, selector);
		return selector;
	}

	public SelectorRule getRuleForSelector(Selector selector, boolean create) {
		SelectorRule rule = (SelectorRule) rulesBySelector.get(selector);
		if (rule == null && create) {
			Selector[] selectors = { selector };
			rule = new SelectorRule(selectors);
			add(rule);
		}
		return rule;
	}

	public void serialize(PrintWriter out) {
		Iterator list=purgeCSStatements().iterator();
		while (list.hasNext()) {
			Object stmt = list.next();
			if (stmt instanceof FontFaceRule) {
				((FontFaceRule) stmt).serialize(out);
				out.println();
			} else if (stmt instanceof BaseRule) {
				((SelectorRule) stmt).serialize(out);
				out.println();
			} else if (stmt instanceof MediaRule) {
				((MediaRule) stmt).serialize(out);
				out.println();
			} else if (stmt instanceof ImportRule) {
				((ImportRule) stmt).serialize(out);
				out.println();
			} else if (stmt instanceof PageRule) {
				((PageRule) stmt).serialize(out);
				out.println();
			}
		}
	}
	/**
	 * 
	 * Get alla Css statements with duplicates 
	 * @return a list of statements with no duplicates
	 */
	private List purgeCSStatements(){
		Map<String,SelectorRule> unique=new HashMap<String, SelectorRule>();
		List statementsOk=new ArrayList();
		
		for (Iterator iterator =  statements.iterator(); iterator.hasNext();) {
			Object r = (Object) iterator.next();
			if (r instanceof SelectorRule) {
				SelectorRule sr=(SelectorRule) r;
				SelectorRule srcache=unique.get(sr.properties.toString());
				if(srcache==null){
					unique.put(sr.properties.toString(), sr);
					srcache=sr;
					continue;
				}
				List<Selector> sels=new ArrayList<Selector>();
				for (int i = 0; i < srcache.selectors.length; i++) {
					sels.add(srcache.selectors[i]);
				}
				for (int i = 0; i < sr.selectors.length; i++) {
					sels.add(sr.selectors[i]);
				}
			
				srcache.selectors=sels.toArray(new Selector[0]);
				
			}else{
				statementsOk.add(r);	
			}
		}
		statementsOk.addAll(unique.values());
		
		return statementsOk;
	}
	
	public Iterator statements() {
		return statements.iterator();
	}
	
	
	

}
