/*
 * Copyright 2013
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
 **/
package de.tudarmstadt.ukp.dkpro.core.commonscodec;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.uima.fit.descriptor.ResourceMetaData;
import org.apache.uima.fit.descriptor.TypeCapability;

/**
 * Double-Metaphone phonetic transcription based on Apache Commons Codec.
 * Works for English.
 */
@ResourceMetaData(name="Commons Codec Double-Metaphone Phonetic Transcriptor")
@TypeCapability(
        inputs={"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"},
        outputs={"de.tudarmstadt.ukp.dkpro.core.api.phonetics.type.PhoneticTranscription"})

public class DoubleMetaphonePhoneticTranscriptor
    extends PhoneticTranscriptor_ImplBase
{

    public DoubleMetaphonePhoneticTranscriptor()
    {
        this.encoder = new DoubleMetaphone();
    }
}
