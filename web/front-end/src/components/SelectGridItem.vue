<template>
<div class="mdl-cell">
  <div class="mdl-card mdl-shadow--2dp extractor-pic-card" :style="{backgroundImage: backgroundImage}">
    <div class="mdl-card__title mdl-card--expand"></div>
    <div class="mdl-card__actions">
      <!-- <label v-mdl class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-1">
        <input class="mdl-checkbox__input" v-model="checked" type="checkbox">
        <span class="mdl-checkbox__label">
          Checkbox
        </span>
      </label> -->
      <a
          v-mdl
          :href="'/api/download?pic='+ encodeURIComponent(this.cardImage)"
          class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored mdl-js-ripple-effect">
        <i class="material-icons">
          file_download
        </i>
        download it
      </a>
    </div>
  </div>
</div>
</template>

<script>
export default {
  props: ['cardImage'],
  components: {},
  data () {
    return {
      checked: false
    }
  },
  computed: {
    backgroundImage () {
      return `url("${this.cardImage}")`
    }
  }
}
</script>

<style lang="css">
.extractor-pic-card {
  background-repeat: no-repeat;
  background-size: contain;
  background-position: center;
}
.extractor-pic-card > .mdl-card__actions {
  background: rgba(0, 0, 0, 0.2);
}
</style>
