<template>
  <div>
    <options/>
    <robot-details/>
  </div>
</template>

<script>
  import Options from './comp/QueryOptions'
  import RobotDetails from './comp/RobotDetails'
  export default {
    name: "RobotModule",
    components: {
      Options,
      RobotDetails
    }
  }
</script>

<style scoped>

</style>
