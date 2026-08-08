/**
 * 3D 场景渲染服务模块
 * 封装所有 Three.js 相关的渲染逻辑、交互和动画处理
 */

// --- 地点数据配置 ---
export const buildingData = [
  { name: 'A座教学楼', type: 'teaching', typeLabel: '教学楼', interactive: true, pos: { x: 283.5, y: 7, z: 55.3 } },
  { name: 'B座教学楼', type: 'teaching', typeLabel: '教学楼', interactive: true, pos: { x: 237.4, y: 7, z: 76.6 } },
  { name: 'C座教学楼', type: 'teaching', typeLabel: '教学楼', interactive: true, pos: { x: 191.3, y: 7, z: 96.3 } },
  { name: 'D座教学楼', type: 'teaching', typeLabel: '教学楼', interactive: true, pos: { x: 148.6, y: 7, z: 114 } },
  { name: 'E座教学楼', type: 'teaching', typeLabel: '教学楼', interactive: true, pos: { x: 114, y: 8, z: 147 } },
  { name: 'F座教学楼', type: 'teaching', typeLabel: '教学楼', interactive: true, pos: { x: 142.4, y: 7, z: 51.4 } },
  { name: 'G座教学楼', type: 'teaching', typeLabel: '教学楼', interactive: true, pos: { x: 191.4, y: 7, z: 28.9 } },
  { name: '文科楼', type: 'teaching', typeLabel: '教学楼', interactive: true, pos: { x: -82.8, y: 6, z: 189.5 } },
  { name: '信息科学与工程学院', type: 'teaching', typeLabel: '学院楼', interactive: true, pos: { x: 474, y: 10, z: 23.5 } },
  { name: '电气工程学院', type: 'teaching', typeLabel: '学院楼', interactive: true, pos: { x: 564, y: 7, z: -50.5 } },
  { name: '理学院', type: 'teaching', typeLabel: '学院楼', interactive: true, pos: { x: 477, y: 6, z: -150 } },
  { name: '机械学院', type: 'teaching', typeLabel: '学院楼', interactive: true, pos: { x: 520, y: 6, z: -170 } },
  { name: '图书馆', type: 'study', typeLabel: '学习中心', interactive: true, pos: { x: 258, y: 11.4, z: -122.8 } },
  { name: '校部', type: 'public', typeLabel: '行政办公', interactive: true, pos: { x: 466, y: 11.5, z: 153.5 } },
  { name: '大学生活动中心', type: 'public', typeLabel: '活动中心', interactive: true, pos: { x: -193, y: 7, z: -62 } },
  { name: '校医院', type: 'public', typeLabel: '医疗服务', interactive: true, pos: { x: -198.5, y: 7, z: 67 } },
  { name: '教师公寓', type: 'dorm', typeLabel: '教工宿舍', interactive: false, pos: { x: -158, y: 7, z: 361 } },
  { name: '超市', type: 'shop', typeLabel: '购物服务', defaultCategory: '购物', interactive: true, pos: { x: -195.5, y: 7, z: -141 } },
  { name: '东门', type: 'gate', typeLabel: '校门', interactive: false, pos: { x: 399.3, y: 7, z: 203 } },
  { name: '南门', type: 'gate', typeLabel: '校门', interactive: false, pos: { x: -414.5, y: 7, z: -102 } },
  { name: '南角门', type: 'gate', typeLabel: '校门', interactive: false, pos: { x: -407.5, y: 7, z: 322.5 } },
  { name: '北门', type: 'gate', typeLabel: '校门', interactive: false, pos: { x: 701.5, y: 6, z: -345 } },
  { name: '南食堂', type: 'canteen', typeLabel: '餐饮服务', defaultCategory: '餐饮', interactive: true, pos: { x: -109, y: 7, z: -221.5 } },
  { name: '南体育场', type: 'sports', typeLabel: '体育设施', interactive: true, pos: { x: -302.8, y: 7, z: -305 } },
  { name: '北食堂', type: 'canteen', typeLabel: '餐饮服务', defaultCategory: '餐饮', interactive: true, pos: { x: 294.5, y: 7, z: -359.5 } },
  { name: '北体育场', type: 'sports', typeLabel: '体育设施', interactive: true, pos: { x: 460, y: 7, z: -446.5 } },
  { name: 'C1宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: -266, y: 7, z: 112 } },
  { name: 'C2宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: -364.2, y: 7, z: 113.7 } },
  { name: 'C3宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: -264.8, y: 7, z: 159.5 } },
  { name: 'C4宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: -363.3, y: 7, z: 160.7 } },
  { name: 'C5宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: -361.8, y: 7, z: 255.4 } },
  { name: 'C6宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: -355.5, y: 7, z: 335.5 } },
  { name: 'C7宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: -358.6, y: 7, z: 447.8 } },
  { name: 'B1宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: -63.3, y: 7, z: -391.7 } },
  { name: 'B2宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: -63.2, y: 7, z: -335.3 } },
  { name: 'A1宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: 307, y: 7, z: -463.5 } },
  { name: 'A2宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: 207, y: 7, z: -477.8 } },
  { name: 'A3宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: 109.8, y: 7, z: -479 } },
  { name: 'A4宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: 238, y: 7, z: -380.2 } },
  { name: 'A5宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: 188.4, y: 7, z: -356 } },
  { name: 'A6宿舍楼', type: 'dorm', typeLabel: '学生宿舍', interactive: false, pos: { x: 138.5, y: 7, z: -331.6 } }
]

// --- Perlin Noise 实现 ---
class ImprovedNoise {
    constructor() {
        this.p = new Uint8Array(512);
        this.shuffle();
    }
    shuffle() {
        for(let i=0; i<256; i++) this.p[i] = i;
        for(let i=0; i<256; i++) {
            let r = Math.floor(Math.random() * 256);
            let t = this.p[i]; this.p[i] = this.p[r]; this.p[r] = t;
        }
        for(let i=0; i<256; i++) this.p[256+i] = this.p[i];
    }
    fade(t) { return t * t * t * (t * (t * 6 - 15) + 10); }
    lerp(t, a, b) { return a + t * (b - a); }
    grad(hash, x, y, z) {
        let h = hash & 15;
        let u = h < 8 ? x : y, v = h < 4 ? y : h === 12 || h === 14 ? x : z;
        return ((h & 1) === 0 ? u : -u) + ((h & 2) === 0 ? v : -v);
    }
    noise(x, y, z) {
        let X = Math.floor(x) & 255, Y = Math.floor(y) & 255, Z = Math.floor(z) & 255;
        x -= Math.floor(x); y -= Math.floor(y); z -= Math.floor(z);
        let u = this.fade(x), v = this.fade(y), w = this.fade(z);
        let A = this.p[X] + Y, AA = this.p[A] + Z, AB = this.p[A + 1] + Z,
            B = this.p[X + 1] + Y, BA = this.p[B] + Z, BB = this.p[B + 1] + Z;
        return this.lerp(w, this.lerp(v, this.lerp(u, this.grad(this.p[AA], x, y, z),
            this.grad(this.p[BA], x - 1, y, z)),
            this.lerp(u, this.grad(this.p[AB], x, y - 1, z),
            this.grad(this.p[BB], x - 1, y - 1, z))),
            this.lerp(v, this.lerp(u, this.grad(this.p[AA + 1], x, y, z - 1),
            this.grad(this.p[BA + 1], x - 1, y, z - 1)),
            this.lerp(u, this.grad(this.p[AB + 1], x, y - 1, z - 1),
            this.grad(this.p[BB + 1], x - 1, y - 1, z - 1))));
    }
}

export class CampusScene3D {
    /**
     * @param {HTMLElement} container - DOM 容器
     * @param {Object} callbacks - 回调函数集合
     * @param {Function} callbacks.onLoad - 加载完成回调
     * @param {Function} callbacks.onError - 错误回调
     * @param {Function} callbacks.onBuildingSelected - 选中建筑回调
     * @param {Function} callbacks.onPanelUpdate - 面板位置更新回调
     */
    constructor(container, callbacks = {}) {
        this.container = container;
        this.callbacks = callbacks;
        
        // Three.js 核心对象
        this.THREE = null;
        this.scene = null;
        this.camera = null;
        this.renderer = null;
        this.labelRenderer = null;
        this.controls = null;
        this.raycaster = null;
        this.pointer = null;
        this.TWEEN = null;
        
        // 场景对象
        this.sunLight = null;
        this.moonLight = null;
        this.ambientLight = null;
        this.hemiLight = null;
        this.celestialObjects = {
            sun: null,
            moon: null,
            clouds: []
        };
        this.layerObjects = {
            courses: [],
            spending: [],
            weather: null
        };
        this.labels = [];
        this.modelObject = null;
        
        // 状态
        this.animationId = null;
        this.selectedBuilding = null;
        this.weather = 'clear';
        this.currentTime = new Date();
    }

    /**
     * 初始化 3D 场景
     */
    async init() {
        try {
            // 动态导入 Three.js 及其依赖
            const threeMod = await import('https://esm.sh/three@0.160.0');
            const { OrbitControls: OC } = await import('https://esm.sh/three@0.160.0/examples/jsm/controls/OrbitControls.js');
            const { FBXLoader: FL } = await import('https://esm.sh/three@0.160.0/examples/jsm/loaders/FBXLoader.js');
            const CSS2DMod = await import('https://esm.sh/three@0.160.0/examples/jsm/renderers/CSS2DRenderer.js');
            const CR = CSS2DMod.CSS2DRenderer || CSS2DMod.default?.CSS2DRenderer;
            const CO = CSS2DMod.CSS2DObject || CSS2DMod.default?.CSS2DObject;
            
            if (!CR) {
                console.error('CSS2DRenderer not found in module', CSS2DMod);
                throw new Error('Failed to load CSS2DRenderer');
            }

            const { default: TweenLib } = await import('https://esm.sh/@tweenjs/tween.js');
            
            this.THREE = threeMod;
            this.CSS2DRenderer = CR;
            this.CSS2DObject = CO;
            this.TWEEN = TweenLib;
            this.OrbitControls = OC;
            this.FBXLoader = FL;

            this.initScene();
            this.initLights();
            this.initCelestialBodies();
            this.initControls();
            
            // 加载模型
            await this.loadModel();
            
            // 绑定事件
            this.bindEvents();
            
            // 开始动画循环
            this.animate();
            
        } catch (e) {
            console.error(e);
            if (this.callbacks.onError) this.callbacks.onError('3D引擎初始化错误');
        }
    }

    initScene() {
        const THREE = this.THREE;
        // 1. 创建场景
        this.scene = new THREE.Scene();
        this.scene.background = new THREE.Color(0x87CEEB);
        this.scene.fog = new THREE.Fog(0x87CEEB, 200, 2000);

        // 2. 配置相机
        // 容器尺寸保护：如果 clientWidth/clientHeight 为 0（DOM 未完成布局），
        // 使用窗口尺寸作为后备，避免创建 0x0 的 Canvas 导致画面不显示
        let w = this.container.clientWidth || window.innerWidth;
        let h = this.container.clientHeight || window.innerHeight;
        this.camera = new THREE.PerspectiveCamera(60, w / h, 0.1, 10000);
        this.camera.position.set(0, 200, 400);

        // 3. 配置 WebGL 渲染器
        this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
        this.renderer.setSize(w, h);
        this.renderer.setPixelRatio(window.devicePixelRatio);
        this.renderer.shadowMap.enabled = true;
        this.container.appendChild(this.renderer.domElement);

        // 4. 配置标签渲染器
        this.labelRenderer = new this.CSS2DRenderer();
        this.labelRenderer.setSize(w, h);
        this.labelRenderer.domElement.style.position = 'absolute';
        this.labelRenderer.domElement.style.top = '0px';
        this.labelRenderer.domElement.style.pointerEvents = 'none';
        this.labelRenderer.domElement.style.zIndex = '1';
        this.container.appendChild(this.labelRenderer.domElement);
    }

    initLights() {
        const THREE = this.THREE;
        // 环境光
        this.ambientLight = new THREE.AmbientLight(0xffffff, 0.4);
        this.scene.add(this.ambientLight);
        
        // 半球光
        this.hemiLight = new THREE.HemisphereLight(0xffffff, 0x444444, 0.3);
        this.hemiLight.position.set(0, 200, 0);
        this.scene.add(this.hemiLight);

        // 太阳光
        this.sunLight = new THREE.DirectionalLight(0xffffff, 1.5);
        this.sunLight.position.set(100, 200, 100);
        this.sunLight.castShadow = true;
        this.sunLight.shadow.mapSize.width = 4096;
        this.sunLight.shadow.mapSize.height = 4096;
        this.sunLight.shadow.camera.near = 0.5;
        this.sunLight.shadow.camera.far = 1500;
        this.sunLight.shadow.camera.left = -800;
        this.sunLight.shadow.camera.right = 800;
        this.sunLight.shadow.camera.top = 800;
        this.sunLight.shadow.camera.bottom = -800;
        this.scene.add(this.sunLight);

        // 月光
        this.moonLight = new THREE.DirectionalLight(0x6688ff, 0.5);
        this.moonLight.position.set(-100, 200, -100);
        this.moonLight.castShadow = true;
        this.moonLight.shadow.mapSize.width = 2048;
        this.moonLight.shadow.mapSize.height = 2048;
        this.moonLight.shadow.camera = this.sunLight.shadow.camera.clone();
        this.moonLight.visible = false;
        this.scene.add(this.moonLight);
    }

    initCelestialBodies() {
        const THREE = this.THREE;
        
        // 太阳
        const sunGeo = new THREE.SphereGeometry(150, 32, 32);
        const sunMat = new THREE.MeshBasicMaterial({ color: 0xffffee, fog: false });
        this.celestialObjects.sun = new THREE.Mesh(sunGeo, sunMat);
        
        // 太阳光晕
        const sunGlowGeo1 = new THREE.SphereGeometry(250, 32, 32);
        const sunGlowMat1 = new THREE.MeshBasicMaterial({ 
            color: 0xffdd00, transparent: true, opacity: 0.6, side: THREE.BackSide, fog: false
        });
        this.celestialObjects.sun.add(new THREE.Mesh(sunGlowGeo1, sunGlowMat1));

        const sunGlowGeo2 = new THREE.SphereGeometry(400, 32, 32);
        const sunGlowMat2 = new THREE.MeshBasicMaterial({ 
            color: 0xffaa00, transparent: true, opacity: 0.3, side: THREE.BackSide, fog: false
        });
        this.celestialObjects.sun.add(new THREE.Mesh(sunGlowGeo2, sunGlowMat2));
        
        this.scene.add(this.celestialObjects.sun);

        // 月亮
        const moonGeo = new THREE.SphereGeometry(120, 32, 32);
        const moonMat = new THREE.MeshBasicMaterial({ color: 0xffffee, fog: false });
        this.celestialObjects.moon = new THREE.Mesh(moonGeo, moonMat);
        
        const moonGlowGeo = new THREE.SphereGeometry(200, 32, 32);
        const moonGlowMat = new THREE.MeshBasicMaterial({ 
            color: 0x6688ff, transparent: true, opacity: 0.3, side: THREE.BackSide, fog: false
        });
        this.celestialObjects.moon.add(new THREE.Mesh(moonGlowGeo, moonGlowMat));
        
        this.scene.add(this.celestialObjects.moon);

        // 云层
        this.createClouds();
    }

    createCloudTexture() {
        const canvas = document.createElement('canvas');
        canvas.width = 64;
        canvas.height = 64;
        const ctx = canvas.getContext('2d');
        
        const grad = ctx.createRadialGradient(32, 32, 0, 32, 32, 32);
        grad.addColorStop(0, 'rgba(255, 255, 255, 0.8)');
        grad.addColorStop(0.4, 'rgba(255, 255, 255, 0.5)');
        grad.addColorStop(1, 'rgba(255, 255, 255, 0)');
        
        ctx.fillStyle = grad;
        ctx.fillRect(0, 0, 64, 64);
        
        return new this.THREE.CanvasTexture(canvas);
    }

    createClouds() {
        const THREE = this.THREE;
        // 清理旧云层
        this.celestialObjects.clouds.forEach(c => this.scene.remove(c));
        this.celestialObjects.clouds = [];

        const perlin = new ImprovedNoise();
        const cloudTexture = this.createCloudTexture();
        const cloudMat = new THREE.SpriteMaterial({ 
            map: cloudTexture,
            color: 0xffffff,
            transparent: true,
            opacity: 0.6,
            depthWrite: false,
        });

        const range = 2000;
        const step = 80;
        
        for (let x = -range; x <= range; x += step) {
            for (let z = -range; z <= range; z += step) {
                const scale = 0.002;
                const noiseVal = perlin.noise(x * scale, 200, z * scale);
                
                if (noiseVal > 0.3) {
                    if (Math.random() > 0.6) continue;

                    const cloudGroup = new THREE.Group();
                    const parts = 4 + Math.floor(Math.random() * 5);
                    
                    for(let i=0; i<parts; i++) {
                        const sprite = new THREE.Sprite(cloudMat.clone());
                        sprite.position.set(
                            (Math.random()-0.5) * 80,
                            (Math.random()-0.5) * 40,
                            (Math.random()-0.5) * 60
                        );
                        const s = 100 + Math.random() * 100;
                        sprite.scale.set(s, s, 1);
                        sprite.material.rotation = Math.random() * Math.PI;
                        sprite.material.opacity = 0.4 + Math.random() * 0.4;
                        
                        cloudGroup.add(sprite);
                    }

                    cloudGroup.position.set(
                        x + (Math.random()-0.5) * 50,
                        600 + noiseVal * 300,
                        z + (Math.random()-0.5) * 50
                    );
                    
                    cloudGroup.userData = {
                        velocity: 0.2 + Math.random() * 0.3
                    };

                    this.scene.add(cloudGroup);
                    this.celestialObjects.clouds.push(cloudGroup);
                }
            }
        }
    }

    initControls() {
        const THREE = this.THREE;
        this.controls = new this.OrbitControls(this.camera, this.renderer.domElement);
        this.controls.enableDamping = true;
        this.controls.maxPolarAngle = Math.PI / 2 - 0.1;

        this.raycaster = new THREE.Raycaster();
        this.pointer = new THREE.Vector2();
    }

    async loadTexture(url) {
        const THREE = this.THREE;
        return new Promise(async (resolve) => {
            try {
                const tl = new THREE.TextureLoader();
                tl.load(url, (t) => resolve(t), undefined, async () => {
                    try {
                        const ibl = new THREE.ImageBitmapLoader();
                        ibl.setOptions({ imageOrientation: 'none', premultiplyAlpha: 'none' });
                        ibl.load(url, (bitmap) => {
                            const t = new THREE.Texture(bitmap);
                            t.needsUpdate = true;
                            resolve(t);
                        }, undefined, () => resolve(null));
                    } catch {
                        resolve(null);
                    }
                });
            } catch {
                resolve(null);
            }
        });
    }

    async loadModel() {
        const THREE = this.THREE;
        const loader = new this.FBXLoader();
        
        return new Promise((resolve, reject) => {
            loader.load('/assets/models/campus.fbx', async (obj) => {
                this.modelObject = obj;
                obj.scale.set(1, 1, 1);
                obj.position.set(20, 0, 0);
                obj.rotation.y = -Math.PI / 2;
                
                try {
                    let tex = await this.loadTexture('/assets/models/textures.webp');
                    if (tex) {
                        if (tex.colorSpace !== undefined && THREE.SRGBColorSpace) {
                            tex.colorSpace = THREE.SRGBColorSpace;
                        } else if (tex.encoding !== undefined && THREE.sRGBEncoding !== undefined) {
                            tex.encoding = THREE.sRGBEncoding;
                        }
                        tex.wrapS = THREE.RepeatWrapping;
                        tex.wrapT = THREE.RepeatWrapping;
                        tex.anisotropy = this.renderer.capabilities.getMaxAnisotropy ? this.renderer.capabilities.getMaxAnisotropy() : 1;
                        tex.minFilter = THREE.LinearMipMapLinearFilter;
                        tex.magFilter = THREE.LinearFilter;
                        tex.generateMipmaps = true;
                        tex.needsUpdate = true;
                    }

                    obj.traverse((child) => {
                        if (child.isMesh) {
                            const makeBasic = () => new THREE.MeshBasicMaterial({
                                map: tex || null,
                                color: 0xffffff
                            });

                            if (Array.isArray(child.material)) {
                                child.material = child.material.map(() => {
                                    const m = makeBasic();
                                    m.side = THREE.DoubleSide;
                                    m.needsUpdate = true;
                                    return m;
                                });
                            } else {
                                const m = makeBasic();
                                m.side = THREE.DoubleSide;
                                m.needsUpdate = true;
                                child.material = m;
                            }
                            child.castShadow = true;
                            child.receiveShadow = true;
                        }
                    });
                } catch (err) {
                    console.error('模型处理出错', err);
                }

                this.scene.add(obj);
                
                // 调整相机
                const box = new THREE.Box3().setFromObject(obj);
                const center = box.getCenter(new THREE.Vector3());
                this.controls.target.copy(center);
                this.camera.position.set(center.x, center.y + 300, center.z + 500);
                
                this.createLabels();
                
                if (this.callbacks.onLoad) this.callbacks.onLoad();
                resolve();
            }, undefined, (err) => {
                if (this.callbacks.onError) this.callbacks.onError('模型加载失败');
                reject(err);
            });
        });
    }

    createLabels() {
        buildingData.forEach(b => {
            const div = document.createElement('div');
            div.className = 'scene-label';
            div.textContent = b.name;
            const label = new this.CSS2DObject(div);
            label.position.set(b.pos.x, b.pos.y + 10, b.pos.z);
            this.scene.add(label);
            this.labels.push(label);
        });
    }

    bindEvents() {
        this.onWindowResize = this.onWindowResize.bind(this);
        this.onCanvasClick = this.onCanvasClick.bind(this);
        window.addEventListener('resize', this.onWindowResize);
        this.renderer.domElement.addEventListener('click', this.onCanvasClick);
    }

    unbindEvents() {
        window.removeEventListener('resize', this.onWindowResize);
        if (this.renderer && this.renderer.domElement) {
            this.renderer.domElement.removeEventListener('click', this.onCanvasClick);
        }
    }

    onWindowResize() {
        if (!this.container) return;
        const w = this.container.clientWidth;
        const h = this.container.clientHeight;
        this.camera.aspect = w / h;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(w, h);
        this.labelRenderer.setSize(w, h);
    }

    onCanvasClick(event) {
        const rect = this.renderer.domElement.getBoundingClientRect();
        this.pointer.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
        this.pointer.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;

        this.raycaster.setFromCamera(this.pointer, this.camera);
        const intersects = this.raycaster.intersectObjects(this.scene.children, true);

        if (intersects.length > 0) {
            const point = intersects[0].point;
            this.moveCameraTo(point);
            this.findNearestBuilding(point);
        } else {
            this.selectedBuilding = null;
            if (this.callbacks.onBuildingSelected) this.callbacks.onBuildingSelected(null);
        }
    }

    moveCameraTo(targetPoint) {
        if (!this.camera || !this.controls || !this.TWEEN) return;
        const THREE = this.THREE;
        const TWEEN = this.TWEEN;

        const offset = new THREE.Vector3().subVectors(this.camera.position, this.controls.target);
        const newCamPos = new THREE.Vector3().addVectors(targetPoint, offset);

        new TWEEN.Tween(this.controls.target)
            .to({ x: targetPoint.x, y: targetPoint.y, z: targetPoint.z }, 1000)
            .easing(TWEEN.Easing.Cubic.Out)
            .start();

        new TWEEN.Tween(this.camera.position)
            .to({ x: newCamPos.x, y: newCamPos.y, z: newCamPos.z }, 1000)
            .easing(TWEEN.Easing.Cubic.Out)
            .start();
    }

    findNearestBuilding(point) {
        let minDist = Infinity;
        let nearest = null;
        
        buildingData.forEach(b => {
            if (!b.interactive) return;
            const dx = point.x - b.pos.x;
            const dz = point.z - b.pos.z;
            const dist = Math.sqrt(dx*dx + dz*dz);
            if (dist < 50) {
                if (dist < minDist) {
                    minDist = dist;
                    nearest = b;
                }
            }
        });

        this.selectedBuilding = nearest;
        if (this.callbacks.onBuildingSelected) this.callbacks.onBuildingSelected(nearest);
    }

    updatePanelPosition() {
        if (!this.selectedBuilding || !this.camera || !this.container) return;
        const THREE = this.THREE;
        const b = this.selectedBuilding;
        const p = new THREE.Vector3(b.pos.x, b.pos.y, b.pos.z);
        
        p.project(this.camera);
        
        const x = (p.x * 0.5 + 0.5) * this.container.clientWidth;
        const y = (-(p.y * 0.5) + 0.5) * this.container.clientHeight;
        
        if (this.callbacks.onPanelUpdate) {
            this.callbacks.onPanelUpdate({ x: x + 20, y: y - 50 });
        }
    }

    getSkyColor(h) {
        const THREE = this.THREE;
        const colors = [
            { h: 0, c: new THREE.Color(0x050510) },
            { h: 5, c: new THREE.Color(0x050510) },
            { h: 6, c: new THREE.Color(0x2a2a5a) },
            { h: 7, c: new THREE.Color(0xff9966) },
            { h: 9, c: new THREE.Color(0x87CEEB) },
            { h: 16, c: new THREE.Color(0x87CEEB) },
            { h: 18, c: new THREE.Color(0xFFD580) },
            { h: 19, c: new THREE.Color(0x2a2a5a) },
            { h: 20, c: new THREE.Color(0x050510) },
            { h: 24, c: new THREE.Color(0x050510) }
        ];
        
        for(let i=0; i<colors.length-1; i++) {
            if(h >= colors[i].h && h <= colors[i+1].h) {
                const t = (h - colors[i].h) / (colors[i+1].h - colors[i].h);
                return colors[i].c.clone().lerp(colors[i+1].c, t);
            }
        }
        return colors[0].c;
    }

    updateEnvironment(date, weatherMode = 'clear') {
        if (!this.scene || !this.sunLight) return;
        const THREE = this.THREE;
        
        this.currentTime = date;
        this.weather = weatherMode;

        const hour = date.getHours() + date.getMinutes() / 60;
        
        // 1. 计算太阳位置
        let sunY, sunX, sunZ;
        let lightIntensity;
        
        if (hour >= 5 && hour <= 19) {
            const theta = ((hour - 6) / 12) * Math.PI;
            sunX = Math.cos(theta) * 4000 * -1;
            sunY = Math.sin(theta) * 3000;
            if (sunY < -200) sunY = -200;
            sunZ = 1000;
            
            lightIntensity = Math.sin(theta) * 1.5;
            if (lightIntensity < 0.1) lightIntensity = 0.1;
            
            if (this.sunLight) {
                this.sunLight.visible = true;
                this.sunLight.intensity = lightIntensity;
                this.sunLight.position.set(sunX/10, sunY/10, sunZ/10);
            }
            if (this.moonLight) this.moonLight.visible = false;
            if (this.celestialObjects.sun) {
                this.celestialObjects.sun.visible = true;
                this.celestialObjects.sun.position.set(sunX, sunY, sunZ);
            }
            if (this.celestialObjects.moon) this.celestialObjects.moon.visible = false;

        } else {
            sunX = 0;
            sunY = -2000;
            sunZ = 2000;
            lightIntensity = 0.05;
            
            if (this.sunLight) this.sunLight.visible = false;
            if (this.celestialObjects.sun) this.celestialObjects.sun.visible = false;
            
            let moonHour = hour;
            if (moonHour < 12) moonHour += 24;
            
            const moonTheta = ((moonHour - 18) / 12) * Math.PI;
            const moonX = Math.cos(moonTheta) * 4000 * -1;
            const moonY = Math.sin(moonTheta) * 2500;
            const moonZ = -1000;
            
            if (this.moonLight) {
                this.moonLight.visible = true;
                this.moonLight.position.set(moonX/10, Math.max(50, moonY/10), moonZ/10);
                this.moonLight.intensity = Math.max(0.2, Math.sin(moonTheta) * 0.6);
            }
            if (this.celestialObjects.moon) {
                this.celestialObjects.moon.visible = true;
                this.celestialObjects.moon.position.set(moonX, Math.max(200, moonY), moonZ);
                this.celestialObjects.moon.lookAt(0,0,0);
            }
        }

        const skyColor = this.getSkyColor(hour);

        this.ambientLight.intensity = Math.max(0.4, lightIntensity * 0.6);
        this.hemiLight.intensity = Math.max(0.3, lightIntensity * 0.5);
        this.hemiLight.groundColor.setHSL(0.6, 1, 0.6);
        this.hemiLight.color.set(skyColor);

        // 更新云层
        if (this.celestialObjects.clouds.length > 0) {
            const isDark = hour < 6 || hour > 18 || weatherMode === 'rain' || weatherMode === 'overcast';
            this.celestialObjects.clouds.forEach(group => {
                group.children.forEach(mesh => {
                    if (weatherMode === 'rain') {
                        mesh.material.color.setHex(0x555555);
                        mesh.material.opacity = 0.9;
                    } else if (weatherMode === 'overcast') {
                        mesh.material.color.setHex(0x888888);
                        mesh.material.opacity = 0.8;
                    } else if (isDark) {
                        mesh.material.color.setHex(0x333344);
                        mesh.material.opacity = 0.5;
                    } else {
                        mesh.material.color.setHex(0xffffff);
                        mesh.material.opacity = 0.8;
                    }
                });
                group.visible = true;
            });
        }

        // 销毁旧粒子
        if (this.layerObjects.weather && this.layerObjects.weather.userData.type !== weatherMode) {
            this.scene.remove(this.layerObjects.weather);
            this.layerObjects.weather.geometry.dispose();
            this.layerObjects.weather.material.dispose();
            this.layerObjects.weather = null;
        }

        if (weatherMode === 'rain' || weatherMode === 'snow') {
            const grayScale = 0.2 + lightIntensity * 0.2;
            const weatherColor = new THREE.Color().setHSL(0.6, 0.1, grayScale);
            this.scene.background = weatherColor;
            this.scene.fog = new THREE.Fog(weatherColor, 150, 1000);
            
            if (!this.layerObjects.weather) {
                this.createWeatherParticles(weatherMode);
            }
        } else if (weatherMode === 'overcast') {
            const overcastColor = new THREE.Color().setHSL(0, 0, 0.4 + lightIntensity * 0.3);
            this.scene.background = overcastColor;
            this.scene.fog = new THREE.Fog(overcastColor, 150, 1000);
        } else {
            this.scene.background = skyColor;
            if (hour >= 6 && hour <= 18) {
                 this.scene.fog = new THREE.Fog(skyColor, 200, 2000);
            } else {
                 this.scene.fog = new THREE.Fog(skyColor, 100, 1200);
            }
        }
    }

    createParticleTexture(type) {
        const THREE = this.THREE;
        const canvas = document.createElement('canvas');
        canvas.width = 32;
        canvas.height = 32;
        const ctx = canvas.getContext('2d');
        
        if (type === 'rain') {
            ctx.strokeStyle = 'rgba(255,255,255,0.8)';
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.moveTo(16, 0);
            ctx.lineTo(16, 32);
            ctx.stroke();
        } else {
            const grad = ctx.createRadialGradient(16,16,0, 16,16,16);
            grad.addColorStop(0, 'rgba(255,255,255,1)');
            grad.addColorStop(1, 'rgba(255,255,255,0)');
            ctx.fillStyle = grad;
            ctx.fillRect(0,0,32,32);
        }
        
        return new THREE.CanvasTexture(canvas);
    }

    createWeatherParticles(type) {
        const THREE = this.THREE;
        const particleCount = 2000;
        const geometry = new THREE.BufferGeometry();
        const positions = [];
        const velocities = [];
        
        for (let i = 0; i < particleCount; i++) {
            const x = Math.random() * 1000 - 500;
            const y = Math.random() * 400;
            const z = Math.random() * 1000 - 500;
            positions.push(x, y, z);
            
            if (type === 'rain') {
                velocities.push(0, -3 - Math.random() * 4, 0);
            } else {
                velocities.push((Math.random() - 0.5) * 0.5, -0.5 - Math.random() * 1.0, (Math.random() - 0.5) * 0.5);
            }
        }
        
        geometry.setAttribute('position', new THREE.Float32BufferAttribute(positions, 3));
        
        const material = new THREE.PointsMaterial({
            color: 0xffffff,
            size: type === 'rain' ? 6 : 4,
            map: this.createParticleTexture(type),
            transparent: true,
            opacity: 0.8,
            depthWrite: false,
            blending: THREE.AdditiveBlending
        });
        
        const particles = new THREE.Points(geometry, material);
        particles.userData = { type: type, velocities: velocities };
        this.scene.add(particles);
        this.layerObjects.weather = particles;
    }

    updateWeatherParticles() {
        if (!this.layerObjects.weather) return;
        
        const particles = this.layerObjects.weather;
        const positions = particles.geometry.attributes.position.array;
        const velocities = particles.userData.velocities;
        const type = particles.userData.type;
        
        for (let i = 0; i < positions.length; i += 3) {
            positions[i] += velocities[i];
            positions[i+1] += velocities[i+1];
            positions[i+2] += velocities[i+2];
            
            let reset = false;
            if (positions[i+1] < 0) reset = true;
            
            if (!reset && positions[i+1] < 60) {
                 for (const b of buildingData) {
                     const dx = positions[i] - b.pos.x;
                     const dz = positions[i+2] - b.pos.z;
                     if (dx*dx + dz*dz < 1600) {
                         reset = true;
                         break;
                     }
                 }
            }
            
            if (reset) {
                positions[i+1] = 300 + Math.random() * 100;
                positions[i] = Math.random() * 1000 - 500;
                positions[i+2] = Math.random() * 1000 - 500;
            }
        }
        particles.geometry.attributes.position.needsUpdate = true;
        
        if (type === 'snow') {
            particles.rotation.y += 0.002;
        }
    }

    updateClouds() {
        this.celestialObjects.clouds.forEach(cloud => {
            cloud.position.x += cloud.userData.velocity;
            if(cloud.position.x > 1200) {
                cloud.position.x = -1200;
                cloud.position.z = (Math.random()-0.5) * 2400;
            }
        });
    }

    toggleCourses(show, todayCourses = []) {
        const THREE = this.THREE;
        if (!this.scene) return;
        
        if (show) {
            const activeBuildings = buildingData.filter(b => b.type === 'teaching' && 
                todayCourses.some(c => c.room && c.room.includes(b.name)));

            activeBuildings.forEach(b => {
                const geo = new THREE.ConeGeometry(5, 20, 32);
                const mat = new THREE.MeshBasicMaterial( {color: 0xffff00} );
                const cone = new THREE.Mesh( geo, mat );
                cone.position.set(b.pos.x, b.pos.y + 30, b.pos.z);
                cone.rotation.z = Math.PI;
                this.scene.add( cone );
                this.layerObjects.courses.push(cone);
            });
        } else {
            this.layerObjects.courses.forEach(o => this.scene.remove(o));
            this.layerObjects.courses = [];
        }
    }

    toggleSpending(show) {
        const THREE = this.THREE;
        if (!this.scene) return;

        if (show) {
            const hotspots = buildingData.filter(b => ['canteen', 'shop'].includes(b.type));
            hotspots.forEach(b => {
               const geometry = new THREE.SphereGeometry(8, 32, 16); 
               const material = new THREE.MeshBasicMaterial( { color: 0xff0000, transparent: true, opacity: 0.6 } ); 
               const sphere = new THREE.Mesh( geometry, material );
               sphere.position.set(b.pos.x, b.pos.y + 15, b.pos.z);
               this.scene.add( sphere );
               this.layerObjects.spending.push(sphere);
            });
        } else {
            this.layerObjects.spending.forEach(o => this.scene.remove(o));
            this.layerObjects.spending = [];
        }
    }

    toggleLabels(show) {
        this.labels.forEach(l => {
            if (l.element) {
                l.element.style.display = show ? 'block' : 'none';
            }
        });
    }

    animate() {
        if (!this.renderer) return;
        
        this.animationId = requestAnimationFrame(this.animate.bind(this));
        
        if (this.TWEEN) this.TWEEN.update();
        if (this.controls) this.controls.update();
        
        this.updateWeatherParticles();
        
        if (this.celestialObjects.clouds.length > 0) {
            this.updateClouds();
        }
        
        if (this.selectedBuilding) {
            this.updatePanelPosition();
        }

        if (this.renderer && this.scene && this.camera) {
            this.renderer.render(this.scene, this.camera);
        }
        if (this.labelRenderer && this.scene && this.camera) {
            this.labelRenderer.render(this.scene, this.camera);
        }
    }


    // ========== 路网可视化系统 ==========
    roadNetworkGroup = null;
    pathLineGroup = null;
    roadNodes = [];
    roadEdges = [];
    startMarker = null;
    endMarker = null;

    /**
     * 加载并渲染路网数据。
     * @param networkData 路网数据 { nodes: [], edges: [] }
     */
    renderRoadNetwork(networkData) {
        const THREE = this.THREE;  // 获取 Three.js 核心对象的局部引用（THREE 通过动态导入存储在 this.THREE）
        this.clearRoadNetwork();
        if (!networkData || !networkData.nodes) return;

        this.roadNetworkGroup = new THREE.Group();
        this.roadNetworkGroup.name = 'roadNetwork';

        // 过滤：只保留路径节点（is_landmark=0），排除地标节点
        // 地标节点与路径节点之间无连接边，独立显示会造成两套互不相连的路网
        const pathNodes = networkData.nodes.filter(n => !n.isLandmark);
        const pathNodeIds = new Set(pathNodes.map(n => n.nodeId));
        // 只保留两端都是路径节点的边
        const pathEdges = (networkData.edges || []).filter(
            e => pathNodeIds.has(e.startNodeId) && pathNodeIds.has(e.endNodeId)
        );

        // 存储节点Map（仅路径节点）
        const nodeMap = new Map();
        for (const node of pathNodes) {
            nodeMap.set(node.nodeId, node);
        }
        this.roadNodes = pathNodes;
        this.roadEdges = pathEdges;

        // 绘制边（先画边，再画节点，使节点在上层）
        // 使用数据库中的 y 坐标（路径节点 y=15，地标节点 y=6~11.5），
        // 使路网悬浮于地面之上，便于观察
        for (const edge of this.roadEdges) {
            const startNode = nodeMap.get(edge.startNodeId);
            const endNode = nodeMap.get(edge.endNodeId);
            if (!startNode || !endNode) continue;

            const points = [
                new THREE.Vector3(startNode.x, startNode.y, startNode.z),
                new THREE.Vector3(endNode.x, endNode.y, endNode.z)
            ];
            const geometry = new THREE.BufferGeometry().setFromPoints(points);
            // 亮青色边线，高不透明度确保对比度
            const material = new THREE.LineBasicMaterial({
                color: 0x00e5ff,
                transparent: true,
                opacity: 0.85
            });
            const line = new THREE.Line(geometry, material);
            this.roadNetworkGroup.add(line);
        }

        // 绘制节点
        // 地标节点：亮橙色，大半径，完全不透明
        // 路径节点：亮黄色，中等半径，高不透明度
        for (const node of networkData.nodes) {
            const isLandmark = node.isLandmark;
            const radius = isLandmark ? 3.0 : 1.5;
            const color = isLandmark ? 0xff6b35 : 0xffeb3b;

            const geometry = new THREE.SphereGeometry(radius, 12, 12);
            const material = new THREE.MeshBasicMaterial({
                color: color,
                transparent: true,
                opacity: isLandmark ? 1.0 : 0.85
            });
            const sphere = new THREE.Mesh(geometry, material);
            // 使用数据库中的 y 坐标，使节点悬浮于对应高度
            sphere.position.set(node.x, node.y, node.z);
            sphere.userData = { nodeId: node.nodeId, nodeName: node.nodeName, isLandmark };
            this.roadNetworkGroup.add(sphere);
        }

        this.scene.add(this.roadNetworkGroup);
    }

    /**
     * 高亮显示路径。
     * @param pathNodes 路径节点数组 [{ nodeId, x, y, z }, ...]
     */
    highlightPath(pathNodes) {
        const THREE = this.THREE;  // 获取 Three.js 核心对象的局部引用（THREE 通过动态导入存储在 this.THREE）
        this.clearPathHighlight();
        if (!pathNodes || pathNodes.length === 0) return;

        this.pathLineGroup = new THREE.Group();
        this.pathLineGroup.name = 'pathHighlight';

        // 绘制路径连线
        // 使用数据库中的 y 坐标，使路径贴合节点实际高度
        const points = pathNodes.map(n => new THREE.Vector3(n.x, n.y, n.z));
        if (points.length >= 2) {
            const geometry = new THREE.BufferGeometry().setFromPoints(points);
            const material = new THREE.LineBasicMaterial({
                color: 0x00ff00,
                linewidth: 3
            });
            const line = new THREE.Line(geometry, material);
            this.pathLineGroup.add(line);

            // 用 TubeGeometry 让路径更粗更明显，半径加大提高可视性
            const curve = new THREE.CatmullRomCurve3(points);
            const tubeGeo = new THREE.TubeGeometry(curve, points.length * 6, 2.5, 8, false);
            const tubeMat = new THREE.MeshBasicMaterial({
                color: 0x00ff00,
                transparent: true,
                opacity: 0.9
            });
            const tube = new THREE.Mesh(tubeGeo, tubeMat);
            this.pathLineGroup.add(tube);
        }

        // 绘制路径节点标记
        // 使用数据库中的 y 坐标，加大半径提高可视性
        for (const node of pathNodes) {
            const geometry = new THREE.SphereGeometry(2.0, 12, 12);
            const material = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
            const sphere = new THREE.Mesh(geometry, material);
            sphere.position.set(node.x, node.y, node.z);
            this.pathLineGroup.add(sphere);
        }

        // 起点标记（红色大球，悬浮于起点节点上方）
        if (pathNodes.length > 0) {
            const start = pathNodes[0];
            const geo = new THREE.SphereGeometry(4, 16, 16);
            const mat = new THREE.MeshBasicMaterial({ color: 0xff0000 });
            const marker = new THREE.Mesh(geo, mat);
            marker.position.set(start.x, start.y + 3, start.z);
            this.pathLineGroup.add(marker);
        }

        // 终点标记（蓝色大球，悬浮于终点节点上方）
        if (pathNodes.length > 1) {
            const end = pathNodes[pathNodes.length - 1];
            const geo = new THREE.SphereGeometry(4, 16, 16);
            const mat = new THREE.MeshBasicMaterial({ color: 0x0000ff });
            const marker = new THREE.Mesh(geo, mat);
            marker.position.set(end.x, end.y + 3, end.z);
            this.pathLineGroup.add(marker);
        }

        this.scene.add(this.pathLineGroup);
    }

    /**
     * 清除路网可视化。
     */
    clearRoadNetwork() {
        if (this.roadNetworkGroup) {
            this.scene.remove(this.roadNetworkGroup);
            this.roadNetworkGroup.traverse(obj => {
                if (obj.geometry) obj.geometry.dispose();
                if (obj.material) obj.material.dispose();
            });
            this.roadNetworkGroup = null;
        }
        this.clearPathHighlight();
    }

    /**
     * 清除路径高亮。
     */
    clearPathHighlight() {
        if (this.pathLineGroup) {
            this.scene.remove(this.pathLineGroup);
            this.pathLineGroup.traverse(obj => {
                if (obj.geometry) obj.geometry.dispose();
                if (obj.material) obj.material.dispose();
            });
            this.pathLineGroup = null;
        }
    }

    /**
     * 切换路网显示/隐藏。
     */
    toggleRoadNetwork(visible) {
        if (this.roadNetworkGroup) {
            this.roadNetworkGroup.visible = visible;
        }
    }


    /**
     * 销毁 3D 场景实例，释放所有资源。
     * 必须正确释放 WebGL 上下文，否则浏览器会因上下文数量上限
     * （通常约16个）导致再次初始化时渲染器创建失败、画面不显示。
     */
    dispose() {
        // 1. 停止动画循环
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
            this.animationId = null;
        }

        // 2. 解绑全局事件监听器
        this.unbindEvents();

        // 3. 清除路网与路径高亮（释放其几何体和材质）
        this.clearRoadNetwork();
        this.clearPathHighlight();

        // 4. 遍历场景，释放所有几何体和材质（防止 GPU 内存泄漏）
        if (this.scene) {
            this.scene.traverse(obj => {
                if (obj.geometry) obj.geometry.dispose();
                if (obj.material) {
                    if (Array.isArray(obj.material)) {
                        obj.material.forEach(m => m.dispose());
                    } else {
                        obj.material.dispose();
                    }
                }
            });
        }

        // 5. 释放 WebGL 渲染器并强制丢失上下文
        //    forceContextLoss() 确保浏览器立即释放 WebGL 上下文，
        //    避免因上下文数量限制导致重新进入页面时渲染器创建失败
        if (this.renderer) {
            this.renderer.dispose();
            if (this.renderer.forceContextLoss) {
                this.renderer.forceContextLoss();
            }
            this.renderer = null;
        }

        // 6. 清空容器 DOM（移除 canvas 和标签渲染器元素）
        if (this.container) {
            this.container.innerHTML = '';
        }

        // 7. 置空所有引用，帮助 GC 回收
        this.scene = null;
        this.camera = null;
        this.labelRenderer = null;
        this.controls = null;
        this.raycaster = null;
        this.pointer = null;
        this.THREE = null;
        this.TWEEN = null;
    }
}
