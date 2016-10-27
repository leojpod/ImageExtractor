var express = require('express')
var path = require('path')
var multer = require('multer')
var upload = multer({ dest: 'pdfs/' })
var fse = require('fs-extra')
var spawn = require('child_process').spawn
var router = express.Router()

/**
 * Brief description of what's required:
  - one POST where we'll upload the PDF to analyse and allow for some customization
 */
router.post('/extract', upload.single('pdfFile'), function (req, res) {
  let pdfFilePath = req.file.path
  let outputPath = path.join(req.app.locals.publicFolder, `/pdf-images/${req.file.filename}/`)
  let child = spawn(
    'java',
    [
      '-jar',
      '../out/artifacts/ImageExtractor_jar/ImageExtractor.jar',
      pdfFilePath,
      req.body.picType,
      outputPath
    ]
  )

  child.stdout.on('data', (data) => {
    console.log(`ok data => '${data}`)
  })
  child.stderr.on('data', (data) => {
    console.log(`err data => '${data}`)
  })
  child.on('close', (code) => {
    console.log(`close code => ${code}`)
    fse.unlink(pdfFilePath)
    if (code === 0) {
      // it worked, fetch the imgs and send the list of URLS to the front-end
      fse.readdir(outputPath, (err, files) => {
        if (err) {
          res.status(500).json({error: `could not read the output folder`, errMessage: err})
          return
        }
        res.status(200).json({
          id: `${req.file.filename}`,
          files: files.map((file) => {
            return `/pdf-images/${req.file.filename}/${file}`
          })
        })
      })
    } else {
      res.status(500).json({error: `an error occured : ${code}`})
    }
  })
})

router.post('/clean/:id', function (req, res) {
  let outputPath = path.join(req.app.locals.publicFolder, `/pdf-images/${req.params.id}`)

  fse.remove(outputPath, (err) => {
    if (err) {
      res.status(500).json({error: `an error occured`, err})
      return
    }
    res.status(200).json({status: 'ok'})
  })
})

router.get('/download/', function (req, res) {
  let picFile = req.query.pic
  if (!picFile) {
    res.status(400).json({error: 'missing required parameter: picFile'})
    return
  }
  picFile = path.join(req.app.locals.publicFolder, picFile)
  console.log('trying to download a picture: ', picFile)
  fse.access(picFile, fse.constants.R_OK, (err) => {
    if (err) {
      res.status(404).json({error: 'image not existing'})
    } else {
      res.download(picFile)
    }
  })
})

module.exports = router
