(() => {
  const canvas = document.querySelector("#colorBends");
  if (!canvas) return;

  const gl = canvas.getContext("webgl", {
    alpha: true,
    antialias: false,
    powerPreference: "low-power",
    premultipliedAlpha: true
  });
  if (!gl) {
    console.error("ColorBends background could not start because WebGL is unavailable.");
    canvas.hidden = true;
    return;
  }
  document.body.classList.add("has-color-bends");

  const vertexSource = `
    attribute vec2 aPosition;
    varying vec2 vUv;
    void main() {
      vUv = aPosition * 0.5 + 0.5;
      gl_Position = vec4(aPosition, 0.0, 1.0);
    }
  `;

  const fragmentSource = `
    precision mediump float;
    #define MAX_COLORS 8
    uniform vec2 uCanvas;
    uniform float uTime;
    uniform vec2 uRotation;
    uniform int uColorCount;
    uniform vec3 uColors[MAX_COLORS];
    uniform float uScale;
    uniform float uFrequency;
    uniform float uWarpStrength;
    uniform vec2 uPointer;
    uniform float uMouseInfluence;
    uniform float uParallax;
    uniform float uNoise;
    uniform float uIntensity;
    uniform float uBandWidth;
    varying vec2 vUv;

    void main() {
      float t = uTime * 0.2;
      vec2 p = vUv * 2.0 - 1.0;
      p += uPointer * uParallax * 0.1;
      vec2 rp = vec2(
        p.x * uRotation.x - p.y * uRotation.y,
        p.x * uRotation.y + p.y * uRotation.x
      );
      vec2 q = vec2(rp.x * (uCanvas.x / uCanvas.y), rp.y);
      q /= max(uScale, 0.0001);
      q /= 0.5 + 0.2 * dot(q, q);
      q += 0.2 * cos(t) - 7.56;
      q += (uPointer - rp) * uMouseInfluence * 0.2;

      vec3 color = vec3(0.0);
      float alpha = 0.0;
      vec2 s = q;
      for (int i = 0; i < MAX_COLORS; ++i) {
        if (i >= uColorCount) break;
        s -= 0.01;
        vec2 r = sin(1.5 * (s.yx * uFrequency) + 2.0 * cos(s * uFrequency));
        float m0 = length(r + sin(5.0 * r.y * uFrequency - 3.0 * t + float(i)) / 4.0);
        float warp = clamp(uWarpStrength, 0.0, 1.0);
        vec2 warped = s + (r - s) * warp * (1.0 + max(uWarpStrength - 1.0, 0.0));
        float m1 = length(warped + sin(5.0 * warped.y * uFrequency - 3.0 * t + float(i)) / 4.0);
        float m = mix(m0, m1, pow(warp, 0.3));
        float weight = 1.0 - exp(-uBandWidth / exp(uBandWidth * m));
        color += uColors[i] * weight;
        alpha = max(alpha, min(weight * 2.5, 0.85));
      }

      color = clamp(color * uIntensity, 0.0, 1.0);
      float grain = fract(sin(dot(gl_FragCoord.xy + vec2(uTime), vec2(12.9898, 78.233))) * 43758.5453);
      color = clamp(color + (grain - 0.5) * uNoise, 0.0, 1.0);
      gl_FragColor = vec4(color * alpha, alpha);
    }
  `;

  function compileShader(type, source) {
    const shader = gl.createShader(type);
    if (!shader) throw new Error("Could not allocate a ColorBends shader.");
    gl.shaderSource(shader, source);
    gl.compileShader(shader);
    if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
      const message = gl.getShaderInfoLog(shader) || "Unknown shader compilation error.";
      gl.deleteShader(shader);
      throw new Error(message);
    }
    return shader;
  }

  let program;
  let vertexShader;
  let fragmentShader;
  try {
    vertexShader = compileShader(gl.VERTEX_SHADER, vertexSource);
    fragmentShader = compileShader(gl.FRAGMENT_SHADER, fragmentSource);
    program = gl.createProgram();
    if (!program) throw new Error("Could not allocate a ColorBends program.");
    gl.attachShader(program, vertexShader);
    gl.attachShader(program, fragmentShader);
    gl.linkProgram(program);
    if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
      throw new Error(gl.getProgramInfoLog(program) || "Unknown shader linking error.");
    }
  } catch (error) {
    console.error("ColorBends background could not initialize.", error);
    canvas.hidden = true;
    return;
  } finally {
    if (vertexShader) gl.deleteShader(vertexShader);
    if (fragmentShader) gl.deleteShader(fragmentShader);
  }

  const positions = gl.createBuffer();
  if (!positions) {
    console.error("ColorBends background could not allocate its drawing buffer.");
    canvas.hidden = true;
    gl.deleteProgram(program);
    return;
  }
  gl.bindBuffer(gl.ARRAY_BUFFER, positions);
  gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([-1, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, 1]), gl.STATIC_DRAW);
  gl.useProgram(program);

  const positionLocation = gl.getAttribLocation(program, "aPosition");
  gl.enableVertexAttribArray(positionLocation);
  gl.vertexAttribPointer(positionLocation, 2, gl.FLOAT, false, 0, 0);

  const uniform = name => gl.getUniformLocation(program, name);
  const uniforms = {
    canvas: uniform("uCanvas"),
    time: uniform("uTime"),
    rotation: uniform("uRotation"),
    colorCount: uniform("uColorCount"),
    colors: uniform("uColors[0]"),
    scale: uniform("uScale"),
    frequency: uniform("uFrequency"),
    warp: uniform("uWarpStrength"),
    pointer: uniform("uPointer"),
    mouseInfluence: uniform("uMouseInfluence"),
    parallax: uniform("uParallax"),
    noise: uniform("uNoise"),
    intensity: uniform("uIntensity"),
    bandWidth: uniform("uBandWidth")
  };
  const colors = new Float32Array([
    1, 0.36, 0.48,
    0.54, 0.36, 1,
    0, 1, 0.82
  ]);
  const pointer = { x: 0, y: 0 };
  const targetPointer = { x: 0, y: 0 };
  const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)");
  let frame = 0;
  let startedAt = 0;
  let width = 0;
  let height = 0;
  let pixelRatio = 1;

  const resize = () => {
    pixelRatio = Math.min(window.devicePixelRatio || 1, 1.5);
    width = Math.max(1, Math.floor(canvas.clientWidth * pixelRatio));
    height = Math.max(1, Math.floor(canvas.clientHeight * pixelRatio));
    if (canvas.width !== width || canvas.height !== height) {
      canvas.width = width;
      canvas.height = height;
      gl.viewport(0, 0, width, height);
      gl.uniform2f(uniforms.canvas, width, height);
    }
    draw(reducedMotion.matches ? 0 : performance.now());
  };

  const draw = now => {
    pointer.x += (targetPointer.x - pointer.x) * 0.06;
    pointer.y += (targetPointer.y - pointer.y) * 0.06;
    const elapsed = reducedMotion.matches ? 0 : (now - startedAt) / 1000;
    const angle = Math.PI / 2 + elapsed * 0.012;
    gl.uniform1f(uniforms.time, elapsed);
    gl.uniform2f(uniforms.rotation, Math.cos(angle), Math.sin(angle));
    gl.uniform2f(uniforms.pointer, pointer.x, pointer.y);
    gl.drawArrays(gl.TRIANGLES, 0, 6);
  };

  const animate = now => {
    draw(now);
    if (!reducedMotion.matches) frame = requestAnimationFrame(animate);
  };

  gl.uniform1i(uniforms.colorCount, 3);
  gl.uniform3fv(uniforms.colors, colors);
  gl.uniform1f(uniforms.scale, 1);
  gl.uniform1f(uniforms.frequency, 1);
  gl.uniform1f(uniforms.warp, 0.8);
  gl.uniform1f(uniforms.mouseInfluence, 0.65);
  gl.uniform1f(uniforms.parallax, 0.5);
  gl.uniform1f(uniforms.noise, 0.08);
  gl.uniform1f(uniforms.intensity, 1.5);
  gl.uniform1f(uniforms.bandWidth, 3);
  gl.enable(gl.BLEND);
  gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);
  gl.clearColor(0, 0, 0, 0);

  const resizeObserver = new ResizeObserver(resize);
  resizeObserver.observe(canvas);
  window.addEventListener("resize", resize);
  window.addEventListener("pointermove", event => {
    const rect = canvas.getBoundingClientRect();
    targetPointer.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
    targetPointer.y = -(((event.clientY - rect.top) / rect.height) * 2 - 1);
  });
  window.addEventListener("blur", () => {
    targetPointer.x = 0;
    targetPointer.y = 0;
  });
  reducedMotion.addEventListener("change", () => {
    cancelAnimationFrame(frame);
    if (reducedMotion.matches) draw(0);
    else {
      startedAt = performance.now();
      frame = requestAnimationFrame(animate);
    }
  });

  resize();
  if (!reducedMotion.matches) {
    startedAt = performance.now();
    frame = requestAnimationFrame(animate);
  }
})();
