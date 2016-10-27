<template>
<form class="mdl-grid" id="pdf-upload-form" name="pdf-upload-form" @submit.stop.prevent="postPDF" style="height: 50vh">
  <div class="mdl-cell mdl-cell--8-col mdl-cell--2-offset mdl-card mdl-shadow--2dp">
    <div class="mdl-card__title mdl-card--expand">
      <h1 class="mdl-card__title-text">Pick a PDF file</h1></div>
    <div class="mdl-card__content mdl-layout">
      <div class="mdl-cell mdl-cell--12-col">
        <input class="hidden" id="pdf-file" required type="file" @change="fileChanged">
        <label class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored" for="pdf-file">
          <i class="material-icons">
            file_upload
          </i>
          <span class="mdl-layout-spacer"></span>
          <span class="emph" v-show="!fileName">Select a file</span>
          <span v-show="fileName">{{fileName}}</span>
        </label>
        <p class="mdl-textfield__error" :class="{'is-invalid': missingFile}">You need to select a file!</p>

      </div>
      <ul>
        <li>
          <label v-mdl class="mdl-radio mdl-js-radio mdl-js-ripple-effect" for="pic-type-jpg">
            <input type="radio" id="pic-type-jpg" class="mdl-radio__button" name="pic-type" value="jpg" v-model="picType">
            <span class="mdl-radio__label">JPEG</span>
          </label>
        </li>
        <li>
          <label v-mdl class="mdl-radio mdl-js-radio mdl-js-ripple-effect" for="pic-type-gif">
            <input type="radio" id="pic-type-gif" class="mdl-radio__button" name="pic-type" value="gif" v-model="picType">
            <span class="mdl-radio__label">GIF</span>
          </label>
        </li>
        <li>
          <label v-mdl class="mdl-radio mdl-js-radio mdl-js-ripple-effect" for="pic-type-png">
            <input type="radio" id="pic-type-png" class="mdl-radio__button" name="pic-type" value="png" v-model="picType">
            <span class="mdl-radio__label">PNG</span>
          </label>
        </li>
        <li class="mdl-textfield__error" :class="{ 'is-invalid': missingPicType }">You need to pick a type!</li>
      </ul>
    </div>
    <div class="mdl-card__actions mdl-card--border">
      <div></div>
      <div class="mdl-layout-spacer"></div><button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored" @click.prevent.stop="postPDF">Extract !</button></div>
  </div>
</form>
</template>

<script>
export default {
  name: 'upload-form',
  data () {
    return {
      file: undefined,
      picType: undefined,
      missings: {
        file: false,
        picType: false
      }
    }
  },
  computed: {
    fileName () {
      return (this.file) ? this.file.name : undefined
    },
    missingFile () {
      return this.missings.file
    },
    missingPicType () {
      return this.missings.picType
    }
  },
  watch: {
    fileName (newFilename) {
      if (newFilename !== undefined) {
        this.missings.file = false
      }
    },
    picType (newPicType) {
      if (newPicType !== undefined) {
        this.missings.picType = false
      }
    }
  },
  methods: {
    fileChanged (evt) {
      if (evt.target.files) {
        this.file = evt.target.files[0]
      }
    },
    postPDF (evt) {
      this.missings.file = !this.file
      this.missings.picType = !this.picType
      if (this.missings.file || this.missings.picType) {
        return
      }
      this.$emit('postPDF', this.file, this.picType)
      console.log('good to go!')
    }
  }
}
</script>

<style>
ul {
  margin: 0;
  list-style-type: none;
}
.is-invalid {
  visibility: visible;
  position: inherit;
  clear: both;
}
</style>
