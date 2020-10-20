/*
$Rev$

The Kyoto Language Modeling Toolkit.
Copyright (C) 2009 Kylm Development Team

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package kylm.reader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of a loader that loads from a text file
 * @author neubig
 *
 */
public class TextFileSentenceReader implements SentenceReader {
	private List<Path> filePaths;

	/**
	 * The constructor, saves the file name and uses the passed in divider
	 * @param fileName The name of the file to be opened
	 * @throws IOException if the file doesn't exist or is unreadable
	 */
	public TextFileSentenceReader(String[] fileName) throws IOException {
	    filePaths = Arrays.stream(fileName)
	        .map(Paths::get)
	        .collect(Collectors.toList())
	        ;

	    Set<Path> invalidPaths = filePaths.stream()
	        .filter(path -> !(Files.isRegularFile(path) && Files.isReadable(path)))
	        .collect(Collectors.toUnmodifiableSet())
	        ;
	    if (!invalidPaths.isEmpty()) {
	        throw new IOException("Some files do not exist or are unreadable: " + invalidPaths);
	    }
	}

	@Override
	public Iterator<String[]> iterator() {
	    try {
    	    return filePaths.stream()
    	        .flatMap(path -> {
                    try {
                        return Files.lines(path);

                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
    	        .map(line -> line.split("\\s+"))
    	        .iterator()
    	        ;

	    } catch (UncheckedIOException e) {
	        e.printStackTrace(System.err);
	        return null;
        }
	}

	@Override
	public boolean supportsReset() {
		return true;
	}
}
