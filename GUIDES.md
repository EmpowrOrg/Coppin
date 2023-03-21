# Adding support for a language

It's really simple to add plugin support for
any [language already supported by CodeMirror](https://codemirror.net/5/mode/).
To add support you need both the mime-type and the url.

#### To find the mime-type

Add the mime-type you wish to add support for. You can find this by looking at the list of supported languages, clicking
on the langauge you want, and then scrolling to the bottom of the page.
Ex: `text/x-kotlin`

#### To find the url

You can find the url needed by going to
the [CodeMirror git folder for their supported modes](https://github.com/codemirror/codemirror5/blob/master/mode/index.html)

Look for the folder name of the language you wish to support.

Ex: If you want to add Kotlin, then you search for Kotlin in the file, and see it's folder name
is `clike`.
Ex:`<li><a href="clike/index.html">Kotlin</a></li>`

Go [here](https://github.com/codemirror/codemirror5/tree/master/mode) and click into the folder name you just discovered
of the language you wish to support.

Notice the name of the `.js` file in this folder.
Ex: If you were adding Kotlin, the js is `clike.js`

Now just append the folder name and the .js name to this
path `https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/`

The final Url for Kotlin would look like: `https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/clike/clike.js`

And now you can just add the mime-type and url to the dictionary like
`"text/x-kotlin": "https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/clike/clike.js",`

#### Unit Test Regex

You most likely don't want to deliver all the text from a Unit Test Error to your user. You will only want
to send the relevant error. Each language allows you to give it a regex that will be used to parse relevant errors
from unit tests.
