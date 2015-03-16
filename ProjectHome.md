An Application that helps to bring list of pages from any of wikimedia projects to local computer.

## Usage ##
### Topic file format ###
Before running application, we need to prepare topic list, list of wiki page arranged in a tree form. Topic list file is a simple text file with its content arranged in a format described below.

  * First line of the file will be always topic tree's heading.
  * Second line should start with an equal character ('=') and it represents a topic node.
  * Subsequent lines may contain wiki page name(s) or topic name(s)
  * Topic name(s) must have one or more equal ('=') characters at beginning of that line.
  * Wiki page names are those with no leading equal characters.
  * Number of equal characters at beginning of a topic name represents depth of that topic node. If a topic name follows only one equal character it will be at depth one, same depth as topic give at second line.
  * A normal line with no leading equal characters will come under last topic it follows.
  * A Topic may increase its depth from last topic by adding one more equal character, but never allowed to increase depth by adding more than one equal characters, because it will prevent forming a tree structure.
  * No blank lines are allowed anywhere in the file.

Sample topic list is given below.
```
Some People
=Artists
Vincent van Gogh
Michelangelo
=Scientists
==Physicists
Albert Einstein
Ernest Rutherford
==Chemists
Ernest Rutherford
=Politicians
Barack Obama
=Sportspeople
==Cricket people
Sachin Tendulkar
Brian Lara
==Footballers
Wayne Rooney
```

### Base directory and template files ###
Next step is to prepare a directory containing at least two files named _toc\_template.html_ and _page\_template.html_. A file named _toc.html_ will be created from the content of _toc\_template.html_. A string {$TOC$} in _toc\_template.html_ will be replaced by table of contents tree that application create from topic list you given. _page\_00000.html_, _page\_00001.html_, ... and so on will be created for each wiki page from the content of _page\_template.html_. Strings {$TITLE$} and {$CONTENT$} in  _page\_template.html_ will be replaced by wiki page title and wiki page content respectively. Any occurrence of these strings ({$TITLE$} and {$CONTENT$}) will be replaced. So you cam use this for placing wiki page title at title tag and under body tag.

### Running application ###
After creating a topic list and setting up a directory, run application.
  1. Click 'Open Topic File' button to load created topic list. If topic list is not in the correct format application will inform you that.
  1. Once topic list was loaded and you see a tree in the tree component. Set directory to which captured contents to be loaded, directory containing _toc\_template.html_ and _page\_template.html_, by clicking on 'Set Base Directory'.
  1. Then start wiki pages capturing process by clicking 'Start' button.
  1. After finishing the capturing process, application will inform you that the process was completed. Directory you set before will contain the captured wiki pages and images, images will be under 'images' sub directory.

### Enhancing ###
You can create an _index.html_ file having two or more frames, one for showing table of contents and one for wiki page, and others for whatever purpose you decide if you need any. Arrange things to show wiki page when user click on a page link on table of contents. You can use CSS to enhance wiki page display like floating images and to hide unwanted parts like 'edit' link in wiki pages, also you can use JavaScript along with CSS to show table of contents.

## Requiremnts ##
This program is written in Java programming language. So Java Runtime Environment (JRE) is required on your machine. For developing it I used JDK 1.6, but I am not sure whether it will run on prior JRE versions like 1.4 and 1.5, because not checked for whether it use any latest API.