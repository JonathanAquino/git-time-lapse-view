Git Time-Lapse View is a cross-platform viewer that downloads all revisions of a
file and lets you scroll through them by dragging a slider. As you scroll, you
are shown a visual diff of the current revision and the previous revision. Thus
you can see how a file evolved, and you can easily find the revision at which
lines appeared, disappeared, or changed.

Time-Lapse View is a powerful visual alternative to the Git "blame"
command. It is inspired by the excellent Time-Lapse View in the Perforce
version-control system.

![Screenshot 1](http://farm3.static.flickr.com/2108/1587747884_5806f7463f_o.png)

Git Time-Lapse View runs on Windows, Mac, Linux, and any platform that runs Java.

![Screenshot 2](http://farm3.static.flickr.com/2017/1608790277_75f459f76e.jpg)


## Installation

Download the program from the [Releases](https://github.com/JonathanAquino/git-time-lapse-view/releases)
page. Then double-click GitTimeLapseView.jar or type java -jar git-time-lapse-view.jar
at a command prompt. The Git Time-Lapse View window will appear.


## Usage

Now let's examine all of the revisions of one of your files. In the "File
Path/URL" field, enter the path to the file in your Git repository, like

    C:\svn\myproject\trunk\myfile.php

There is also a field that lets you limit the number of revisions to download.
Finally, hit the Load button. You'll see a progress bar as the revisions are downloaded.

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

You can also use the Browse Directories button ("...") to choose a file.


## Build Instructions

1. Open your favorite Java IDE.
2. Add src and test as source paths.
3. Add the jars in lib and lib/test.
4. Run com.jonathanaquino.gittimelapseview.Application.


## Libraries

The author gratefully acknowledges the use of the following libraries.

    - JGit. Git client library. https://eclipse.org/jgit/
    - Apache Commons Lang. Java utility functions. http://commons.apache.org/
    - JArgs. Command-line parsing. http://jargs.sourceforge.net/
    - OpenJUMP (author's past project). Various Java snippets. http://openjump.org/
    - Java Diff. Diff algorithm. http://www.incava.org/projects/java/java-diff/
    - FatJar. Easy single-jar deployment. http://fjep.sourceforge.net/
    - svn-time-lapse-view. The original. https://github.com/JonathanAquino/svn-time-lapse-view


## Contact

Jonathan Aquino  -  jonathan.aquino@gmail.com  -  http://jonathanaquino.com
Kim Tiedemann  -  http://www.tiede.dk:8080/roller/kim/