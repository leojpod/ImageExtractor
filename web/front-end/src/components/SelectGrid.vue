<template>
<div class="mdl-grid">
  <template v-for="picUrl in picUrls">
    <select-grid-item :cardImage="picUrl"></select-grid-item>
  </template>
  <div class="mdl-cell">
    <button
        v-mdl
        type="button"
        class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect"
        @click.prevent.stop="done">
      <i class="material-icons">arrow_back</i>
      Try with another PDF
    </button>
    <small>Or another type of picture...</small>
  </div>
</div>
</template>

<script>
import SelectGridItem from './SelectGridItem'

export default {
  props: ['picUrls'],
  components: {
    SelectGridItem
  },
  data () {
    return {}
  },
  computed: {},
  methods: {
    done () {
      this.$emit('done')
    }
  }
}
</script>

<style lang="css">
</style>
