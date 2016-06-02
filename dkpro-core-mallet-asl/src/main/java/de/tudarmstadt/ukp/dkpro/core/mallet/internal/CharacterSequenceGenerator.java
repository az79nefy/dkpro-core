/*******************************************************************************
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tudarmstadt.ukp.dkpro.core.mallet.internal;

import cc.mallet.types.TokenSequence;
import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.jcas.JCas;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link TokenSequenceGenerator} that generates character based tokens.
 */
public class CharacterSequenceGenerator
        extends TokenSequenceGenerator
{
    public static final String WHITESPACE_CHAR_REPLACEMENT = "</s>";

    @Override
    public List<TokenSequence> tokenSequences(JCas aJCas)
            throws FeaturePathException
    {
        if (getCoveringTypeName().isPresent()) {
            Type coveringType = aJCas.getTypeSystem().getType(getCoveringTypeName().get());
            return CasUtil.select(aJCas.getCas(), coveringType).stream()
                    .map(AnnotationFS::getCoveredText)
                    .map(this::characterSequence)
                    .collect(Collectors.toList());
        }
        else {
            return Collections.singletonList(characterSequence(aJCas.getDocumentText()));
        }
    }

    /**
     * Generate a token sequence where each 'token' is a character. Non-alphabet characters are omitted.
     *
     * @param text a string
     * @return a {@link TokenSequence}
     */
    private TokenSequence characterSequence(String text)
    {
        if (isLowercase()) {
            text = text.toLowerCase();
        }
        return new TokenSequence(text.chars()
                .mapToObj(i -> (char) i)
                /* filter out strange characters */
                .filter(c -> Character.isAlphabetic(c) || Character.isDigit(c) || Character
                        .isWhitespace(c))
                /* replace whitespace characters */
                .map(c -> Character.isWhitespace(c) ? WHITESPACE_CHAR_REPLACEMENT : c)
                .toArray());
    }

}
