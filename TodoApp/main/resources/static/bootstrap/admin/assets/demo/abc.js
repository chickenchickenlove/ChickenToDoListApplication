<canvas id="example" width="200" height="200">
    This text is displayed if your browser does not support HTML5 Canvas.
</canvas>
<script>
    var example = document.getElementById('example');
    var context = example.getContext('2d');
    context.fillStyle = 'red';
    context.fillRect(30, 30, 50, 50);
</script>