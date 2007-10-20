This is a README.txt file for the SVN Time-Lapse View revision browser.


DESCRIPTION

SVN Time-Lapse View is a cross-platform viewer that downloads all revisions of a
file and lets you scroll through them by dragging a slider. As you scroll, you
are shown a visual diff of the current revision and the previous revision. Thus
you can see how a file evolved, and you can easily find the revision at which
lines appeared, disappeared, or changed.

Time-Lapse View is a powerful visual alternative to the Subversion "blame"
command. It is inspired by the excellent Time-Lapse View in the Perforce
version-control system.


USAGE

To start the program, simply double-click SvnTimeLapseView.jar or type java -jar
SvnTimeLapseView.jar at a command prompt. The SVN Time-Lapse View window will
appear.

Now let's examine all of the revisions of one of your files. In the "File
Path/URL" field, enter the file's SVN URL, for example,

    http://svn.myproject.com/repos/svnkit/trunk/myfile.php

or the path to the file in your Subversion workspace, like

    C:\svn\myproject\trunk\myfile.php

If necessary, enter your username and password in the next two fields. The last
field lets you limit the number of revisions to download (default 100). Finally,
hit the Load button. You'll see a progress bar as the revisions are downloaded.

Now comes the fun part. You can drag the slider at the top to scroll through all
of the revisions of the file. Changes between each revision are highlighted in
blue. As an alternative to the slider, you can click the left and right buttons
in the upper-right corner.

The number of differences is indicated in the lower-right corner. There are up
and down arrows for moving from one difference to the next. Additionally, a
search box is provided in the lower-left corner.

You can also control the four arrow buttons using Alt+Left, Alt+Right, Alt+Up,
and Alt+Down.

You might find it more convenient to view only the differences instead of the
entire file. Try selecting the Show Differences Only checkbox at the bottom of
the window.


LICENSING

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 2 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 51 Franklin
Street, Fifth Floor, Boston, MA 02110-1301, USA.


LIBRARIES

The author gratefully acknowledges the use of the following libraries.

    - SVNKit. Subversion client library. http://svnkit.com/
    - Apache Commons Lang. Java utility functions. http://commons.apache.org/
    - JArgs. Command-line parsing. http://jargs.sourceforge.net/
    - OpenJUMP (author's past project). Various Java snippets. http://openjump.org/
    - Java Diff. Diff algorithm. http://www.incava.org/projects/java/java-diff/
    - FatJar. Easy single-jar deployment. http://fjep.sourceforge.net/


RESOURCES

Web Site: http://code.google.com/p/svn-time-lapse-view/
Issue Tracker: http://code.google.com/p/svn-time-lapse-view/issues/list
Source Code: http://code.google.com/p/svn-time-lapse-view/source
FatJar (easy single-jar deployment): http://fjep.sourceforge.net/


CONTACTS

For all questions related to SVN Time-Lapse View please contact Jonathan Aquino
at jonathan.aquino@gmail.com