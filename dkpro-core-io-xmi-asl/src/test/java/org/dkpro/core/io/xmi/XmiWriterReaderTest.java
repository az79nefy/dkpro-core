/*
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dkpro.core.io.xmi;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.JCasFactory.createText;
import static org.apache.uima.fit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasCreationUtils;
import org.dkpro.core.api.io.ResourceCollectionReaderBase;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.testing.DkproTestContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

public class XmiWriterReaderTest
{
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void thatWritingAndReadingXML1_1works() throws Exception
    {
        File outputFolder = testContext.getTestOutputFolder();
        
        JCas outDocument = createText(
                readFileToString(new File("src/test/resources/texts/chinese.txt"), UTF_8), "zh");
        
        DocumentMetaData dmd = DocumentMetaData.create(outDocument);
        dmd.setDocumentId("output.xmi");
        
        AnalysisEngine writer = createEngine(XmiWriter.class, 
                XmiWriter.PARAM_TARGET_LOCATION, outputFolder,
                XmiWriter.PARAM_STRIP_EXTENSION, true,
                XmiWriter.PARAM_VERSION, "1.1",
                XmiWriter.PARAM_OVERWRITE, true);
        
        writer.process(outDocument);
        
        JCas inDocument = JCasFactory.createJCas();
        
        CollectionReader reader = createReader(XmiReader.class, 
                XmiReader.PARAM_SOURCE_LOCATION, new File(outputFolder, "output.xmi"));
        reader.getNext(inDocument.getCas());
        
        assertThat(outDocument.getDocumentText()).isEqualTo(inDocument.getDocumentText());
    }
    
    @Test
    public void test() throws Exception
    {
        write();
        read();
    }

    public void write() throws Exception
    {
        CollectionReader textReader = CollectionReaderFactory.createReader(
                TextReader.class,
                ResourceCollectionReaderBase.PARAM_SOURCE_LOCATION, "src/test/resources/texts",
                ResourceCollectionReaderBase.PARAM_PATTERNS, "latin.txt",
                ResourceCollectionReaderBase.PARAM_LANGUAGE, "latin");

        AnalysisEngine xmiWriter = AnalysisEngineFactory.createEngine(
                XmiWriter.class,
                XmiWriter.PARAM_TARGET_LOCATION, testFolder.getRoot().getPath());

        runPipeline(textReader, xmiWriter);

        assertTrue(new File(testFolder.getRoot(), "latin.txt.xmi").exists());
    }

    public void read() throws Exception
    {
        CollectionReader xmiReader = CollectionReaderFactory.createReader(
                XmiReader.class,
                ResourceCollectionReaderBase.PARAM_SOURCE_LOCATION, testFolder.getRoot().getPath(),
                ResourceCollectionReaderBase.PARAM_PATTERNS, "*.xmi");

        CAS cas = CasCreationUtils.createCas(createTypeSystemDescription(), null, null);
        xmiReader.getNext(cas);

        String refText = readFileToString(new File("src/test/resources/texts/latin.txt"));
        assertEquals(refText, cas.getDocumentText());
        assertEquals("latin", cas.getDocumentLanguage());
    }

    @Rule
    public DkproTestContext testContext = new DkproTestContext();
}