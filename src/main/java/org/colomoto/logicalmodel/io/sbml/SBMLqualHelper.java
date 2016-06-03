package org.colomoto.logicalmodel.io.sbml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.LayoutModelPlugin;
import org.sbml.jsbml.ext.qual.QualConstants;
import org.sbml.jsbml.ext.qual.QualModelPlugin;
import org.sbml.jsbml.xml.stax.SBMLReader;

/**
 * Helper to create new SBML documents from scratch or from files using JSBML.
 * It will create a bundle with a convenient access to the document,
 * its enclosed model and the qualitative part of the model.
 * 
 * @author Aurelien Naldi
 * @ Some changes by Orlando Rocha
 */
public class SBMLqualHelper {

	public static SBMLQualBundle loadFile(File f) throws IOException, XMLStreamException {
		return getQualitativeModel( parseFile(f));
	}
	
	public static SBMLQualBundle loadSBMLDocument(SBMLDocument doc) throws IOException{
		if(doc.getVersion()>0 && doc.getLevel()>2)
			return getQualitativeModel(doc);
		else
			throw new IOException("Invalid SBML Qual file");
	}
	
	public static SBMLQualBundle parseInputStream(InputStream in) throws XMLStreamException {
		return getQualitativeModel( new SBMLReader().readSBMLFromStream(in));
	}
	
	public static SBMLDocument parseFile(File f) throws IOException, XMLStreamException {
		
		return new SBMLReader().readSBML(f);
	}

	/**
	 * Create a new Bundle, with an empty qualitative model
	 * 
	 * @return a new SBML document
	 */
	public static SBMLQualBundle newBundle() {
		return newBundle(false);
	}
	public static SBMLQualBundle newBundle(boolean addLayout) {
		// init SBML document
		SBMLDocument sdoc = new SBMLDocument(3,1);
		
		sdoc.enablePackage(QualConstants.namespaceURI);
		
		if (addLayout) {
			sdoc.addDeclaredNamespace(LayoutConstants.shortLabel, LayoutConstants.namespaceURI);
		}

		// create the main SBML model
		Model smodel = sdoc.createModel("model_id");
		
		// add qual and layout extensions
		QualModelPlugin qmodel = new QualModelPlugin(smodel);
		smodel.addExtension(QualConstants.namespaceURI, qmodel);

		LayoutModelPlugin lmodel = null;
		if (addLayout) {
			lmodel = new LayoutModelPlugin(smodel);
			smodel.addExtension(LayoutConstants.namespaceURI, lmodel);
			sdoc.getSBMLDocumentAttributes().put(LayoutConstants.shortLabel + ":required", "false");
		}

		return new SBMLQualBundle(sdoc, smodel, qmodel, lmodel);
	}
	
	
	private static SBMLQualBundle getQualitativeModel(SBMLDocument sdoc) {
		Model smodel = sdoc.getModel();
		
		QualModelPlugin qmodel = (QualModelPlugin) smodel.getExtension(QualConstants.shortLabel);

		String layoutnamespaceuri=LayoutConstants.getNamespaceURI(smodel.getLevel(), smodel.getVersion());
		LayoutModelPlugin lmodel = (LayoutModelPlugin) smodel.getExtension(layoutnamespaceuri);

		return new SBMLQualBundle(sdoc, smodel, qmodel, lmodel);
	}
}
