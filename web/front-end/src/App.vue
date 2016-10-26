<template>
  <div id="app">
    <upload-form v-if="step === 'form'" @postPDF='postPDF'></upload-form>
    <div v-if="step === 'loading'" class="spinner-container mdl-grid">
      <div class="mdl-cell mdl-cell--middle mdl-cell--strech mdl-typography--text-center">
        <div v-mdl class="mdl-spinner mdl-js-spinner is-active"></div>
      </div>
    </div>
    <select-grid v-if="step === 'results' " :files="files" @done="cleanup"></select-grid>
    <toast :message="message" :type="type"></toast>
  </div>
</template>

<script>
/* global FormData */
import UploadForm from './components/UploadForm'
import SelectGrid from './components/SelectGrid'
import Toast from './components/Toast'

export default {
  name: 'app',
  data () {
    return {
      step: 'form',
      message: undefined,
      type: undefined,
      files: undefined,
      pdfId: undefined
    }
  },
  components: {
    UploadForm,
    SelectGrid,
    Toast
  },
  methods: {
    postPDF (file, picType) {
      console.log('App.postPDF starting')
      let formData = new FormData()
      formData.append('pdfFile', file)
      formData.append('picType', picType)
      this.step = 'loading'
      this.message = undefined
      this.$http.post('/api/extract', formData).then((response) => {
        this.step = 'results'
        console.log('api extract returned ! -> ', arguments)
        console.log('response -> ', response)

        this.message = 'analysis completed'
        this.type = 'info'
      }, (response) => {
        this.step = 'form'
        console.log('api extract returned with an error ! -> ', arguments)
        console.log('response -> ', response)
        this.type = 'error'
        this.message = 'something went wrong with your request'
      })
    },
    cleanup () {
      this.$http.post(`/clean/${this.pdfId}`).then((response) => {
        this.type = 'info'
        this.message = 'treatment completed'
        this.step = 'form'
      }, (response) => {
        this.type = 'error'
        this.message = 'clean up didn\'t work'
      })
    }
  }
}
</script>

<style>
.spinner-container {
  align-items: center;
  justify-content: center;
  height: calc(100vh - 56px); /* full screen - header's height */
}
</style>
