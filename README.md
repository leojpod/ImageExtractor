# ImageExtractor

Small tool to extract files from PDF.

## How to build?

 - ***Prepare the JAR:*** use `MANIFEST.MF` to build the jars and place them in `web/jars`. There should be 3 jars: `bfopdf-license.jar`, `bfopdf.jar` & `ImageExtractor.jar`

 - ***Build the front-end:*** in a console follow the next steps


    # check out the front-end directory
    $ cd [SomePath]/ImageExtractor/front-end/

    # install the dependencies the first time you're building that project:
    $ npm install && bower install

    # then use webpack to build the files and place when in the express project:
    $ npm run build

    # all done for the front-end!

 - ***Launch the back-end:*** in a console follow the next steps


    # check out the back-end directory
    $ cd [SomePath]/ImageExtractor/web/

    # install the dependencies:
    $ npm install

    # run the project:
    $ npm run start

Open `localhost:3000`

# How to *dev-build*?

 - ***Prepare the JAR:*** same thing as for a regular build

 - ***Build the front-end:*** in a console follow the next steps


    # check out the front-end directory
    $ cd [SomePath]/ImageExtractor/front-end/

    # use webpack to get some auto-reload features and other nice things for dev
    $ npm run dev


 - ***Launch the back-end:*** in **another** console follow the next steps


    # check out the back-end directory
    $ cd [SomePath]/ImageExtractor/web/

    # run with nodemon to restart the back-end after each change on the server
    $ DEBUG=web:* npm run debug

Open `localhost:8081`!
