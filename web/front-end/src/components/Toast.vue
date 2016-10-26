<template>
  <div v-mdl class="mdl-js-snackbar mdl-snackbar" >
    <div class="mdl-snackbar__text" >
    </div>
    <button class="mdl-snackbar__action" type="button"></button>
  </div>
</template>

<script>
export default {
  name: 'toast',
  props: ['message', 'type'],
  // computed: {
  //   icon () {
  //     switch (this.type) {
  //       case 'error':
  //         return 'error'
  //       case 'info':
  //       default:
  //         return 'info_outline'
  //     }
  //   }
  // },
  watch: {
    message (newMessage) {
      if (newMessage) {
        this.showToast()
      }
    }
  },
  methods: {
    showToast () {
      this.$el.MaterialSnackbar.showSnackbar({message: this.message})
    }
  }
}
</script>

<style>
</style>
