/* global componentHandler */
import Vue from 'vue'
import VueResource from 'vue-resource'
import App from './App'

Vue.directive('mdl', {
  bind (el) {
    componentHandler.upgradeElement(el)
  }
})
Vue.use(VueResource)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  components: { App }
})
