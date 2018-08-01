<template>
  <div>
    <options/>
    <preview-details/>
  </div>
</template>

<script>
  import Options from './comp/QueryOptions'
  import PreviewDetails from './comp/PreviewDetails'

  export default {
    name: "IoPreview",
    components: {
      Options,
      PreviewDetails
    }
  }
</script>

<style scoped>

</style>
