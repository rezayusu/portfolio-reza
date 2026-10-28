const BADGE_ICON_CDN = "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/";
const BADGE_ICON_LIBRARY = [
  { name: "JavaScript", short: "JS", slug: "javascript/javascript-original.svg", link: "https://developer.mozilla.org/en-US/docs/Web/JavaScript", color: "#F7DF1E" },
  { name: "TypeScript", short: "TS", slug: "typescript/typescript-original.svg", link: "https://www.typescriptlang.org", color: "#3178C6" },
  { name: "Java", short: "Java", slug: "java/java-original.svg", link: "https://www.java.com", color: "#007396" },
  { name: "Spring", short: "Spring", slug: "spring/spring-original.svg", link: "https://spring.io", color: "#6DB33F" },
  { name: "Python", short: "Py", slug: "python/python-original.svg", link: "https://www.python.org", color: "#3776AB" },
  { name: "React", short: "React", slug: "react/react-original.svg", link: "https://react.dev", color: "#61DAFB" },
  { name: "Next.js", short: "Next", slug: "nextjs/nextjs-original.svg", link: "https://nextjs.org", color: "#111111" },
  { name: "Vue.js", short: "Vue", slug: "vuejs/vuejs-original.svg", link: "https://vuejs.org", color: "#4FC08D" },
  { name: "Angular", short: "Angular", slug: "angularjs/angularjs-original.svg", link: "https://angular.dev", color: "#DD0031" },
  { name: "Node.js", short: "Node", slug: "nodejs/nodejs-original.svg", link: "https://nodejs.org", color: "#339933" },
  { name: "HTML5", short: "HTML", slug: "html5/html5-original.svg", link: "https://developer.mozilla.org", color: "#E34F26" },
  { name: "CSS3", short: "CSS", slug: "css3/css3-original.svg", link: "https://developer.mozilla.org", color: "#1572B6" },
  { name: "Tailwind CSS", short: "Tailwind", slug: "tailwindcss/tailwindcss-original.svg", link: "https://tailwindcss.com", color: "#06B6D4" },
  { name: "Bootstrap", short: "BS", slug: "bootstrap/bootstrap-original.svg", link: "https://getbootstrap.com", color: "#7952B3" },
  { name: "PostgreSQL", short: "PG", slug: "postgresql/postgresql-original.svg", link: "https://www.postgresql.org", color: "#4169E1" },
  { name: "MySQL", short: "SQL", slug: "mysql/mysql-original.svg", link: "https://www.mysql.com", color: "#4479A1" },
  { name: "MongoDB", short: "Mongo", slug: "mongodb/mongodb-original.svg", link: "https://www.mongodb.com", color: "#47A248" },
  { name: "Redis", short: "Redis", slug: "redis/redis-original.svg", link: "https://redis.io", color: "#FF4438" },
  { name: "Firebase", short: "Firebase", slug: "firebase/firebase-plain.svg", link: "https://firebase.google.com", color: "#FFCA28" },
  { name: "PHP", short: "PHP", slug: "php/php-original.svg", link: "https://www.php.net", color: "#777BB4" },
  { name: "Laravel", short: "Laravel", slug: "laravel/laravel-original.svg", link: "https://laravel.com", color: "#FF2D20" },
  { name: "Go", short: "Go", slug: "go/go-original.svg", link: "https://go.dev", color: "#00ADD8" },
  { name: "C++", short: "C++", slug: "cplusplus/cplusplus-original.svg", link: "https://isocpp.org", color: "#00599C" },
  { name: "C#", short: "C#", slug: "csharp/csharp-original.svg", link: "https://learn.microsoft.com", color: "#239120" },
  { name: "Flutter", short: "Flutter", slug: "flutter/flutter-original.svg", link: "https://flutter.dev", color: "#02569B" },
  { name: "Android", short: "Android", slug: "android/android-original.svg", link: "https://developer.android.com", color: "#34A853" },
  { name: "Docker", short: "Docker", slug: "docker/docker-original.svg", link: "https://www.docker.com", color: "#2496ED" },
  { name: "Kubernetes", short: "K8s", slug: "kubernetes/kubernetes-plain.svg", link: "https://kubernetes.io", color: "#326CE5" },
  { name: "Git", short: "Git", slug: "git/git-original.svg", link: "https://git-scm.com", color: "#F05032" },
  { name: "GitHub", short: "GitHub", slug: "github/github-original.svg", link: "https://github.com", color: "#181717" },
  { name: "VS Code", short: "VS", slug: "vscode/vscode-original.svg", link: "https://code.visualstudio.com", color: "#007ACC" },
  { name: "Postman", short: "Postman", slug: "postman/postman-original.svg", link: "https://www.postman.com", color: "#FF6C37" },
  { name: "Figma", short: "Figma", slug: "figma/figma-original.svg", link: "https://www.figma.com", color: "#F24E1E" },
  { name: "Vite", short: "Vite", slug: "vitejs/vitejs-original.svg", link: "https://vitejs.dev", color: "#646CFF" },
  { name: "Linux", short: "Linux", slug: "linux/linux-original.svg", link: "https://www.linux.org", color: "#FCC624" },
  { name: "Nginx", short: "Nginx", slug: "nginx/nginx-original.svg", link: "https://nginx.org", color: "#009639" },
  { name: "Prisma", short: "Prisma", slug: "prisma/prisma-original.svg", link: "https://www.prisma.io", color: "#2D3748" }
];

function getBadgeIconUrl(slug) {
  return BADGE_ICON_CDN + slug;
}

function markPickedBadgeTile(picker) {
  const iconUrlInput = picker.closest("label.badge-icon-field")?.querySelector("input[type='url']");
  const current = (iconUrlInput?.value || "").trim().toLowerCase();
  picker.querySelectorAll(".badge-tile").forEach(tile => {
    const tileIcon = (tile.dataset.icon || "").toLowerCase();
    tile.classList.toggle("is-picked", current !== "" && (tileIcon === current || current.includes(tile.dataset.slug || "")));
  });
}

function pickerApplySelection(picker, tile) {
  const iconUrlInput = picker.closest("label.badge-icon-field")?.querySelector("input[type='url']");
  const form = tile.closest("form");
  if (!iconUrlInput || !form) return;
  iconUrlInput.value = tile.dataset.icon;
  const linkInput = form.elements["badgeLink"];
  if (linkInput) linkInput.value = tile.dataset.link || "";
  const nameInput = form.elements["name"];
  if (nameInput && !nameInput.value.trim()) nameInput.value = tile.dataset.name;
  const badgeInput = form.elements["badge"];
  if (badgeInput) badgeInput.value = tile.dataset.short;
  const colorInput = form.elements["accentColor"];
  if (colorInput) colorInput.value = (tile.dataset.color || "").toUpperCase();
  markPickedBadgeTile(picker);
}

function renderBadgePickers(root = document) {
  root.querySelectorAll(".badge-picker").forEach(picker => {
    const grid = picker.querySelector(".badge-picker-grid");
    if (!grid) return;
    
    if (grid.dataset.ready !== "true") {
      grid.innerHTML = BADGE_ICON_LIBRARY.map(entry => {
        const iconUrl = getBadgeIconUrl(entry.slug);
        return `<button type="button" class="badge-tile" data-icon="${iconUrl}" data-link="${entry.link}" data-name="${entry.name}" data-short="${entry.short}" data-color="${entry.color}" data-slug="${entry.slug}" data-key="${(entry.name + " " + entry.slug).toLowerCase()}" title="${entry.name}">
          <img loading="lazy" src="${iconUrl}" alt="" width="24" height="24"><span>${entry.name}</span>
        </button>`;
      }).join("");

      grid.addEventListener("click", event => {
        const tile = event.target.closest(".badge-tile");
        if (tile) pickerApplySelection(picker, tile);
      });
    }

    const search = picker.querySelector(".badge-picker-search");
    if (search && search.dataset.ready !== "true") {
      search.dataset.ready = "true";
      search.addEventListener("input", () => {
        const query = search.value.trim().toLowerCase();
        grid.querySelectorAll(".badge-tile").forEach(tile => {
          tile.hidden = query !== "" && !tile.dataset.key.includes(query);
        });
      });
    }

    const clear = picker.querySelector(".badge-picker-clear");
    if (clear && clear.dataset.ready !== "true") {
      clear.dataset.ready = "true";
      clear.addEventListener("click", () => {
        const iconUrlInput = picker.closest("label.badge-icon-field")?.querySelector("input[type='url']");
        if (iconUrlInput) iconUrlInput.value = "";
        const form = picker.closest("form");
        const linkInput = form?.elements["badgeLink"];
        if (linkInput) linkInput.value = "";
        markPickedBadgeTile(picker);
      });
    }

    const toggleBtn = picker.closest("label.badge-icon-field")?.querySelector(".badge-picker-toggle");
    const container = picker.querySelector(".badge-picker-content");
    if (toggleBtn && container && toggleBtn.dataset.ready !== "true") {
      toggleBtn.dataset.ready = "true";
      toggleBtn.addEventListener("click", () => {
        const isCollapsed = container.classList.toggle("is-collapsed");
        toggleBtn.textContent = isCollapsed ? "Pilih dari Galeri Icon ▾" : "Tutup Galeri ▴";
      });
    }

    markPickedBadgeTile(picker);
    grid.dataset.ready = "true";
  });
}

document.addEventListener("DOMContentLoaded", () => {
  renderBadgePickers();

  const adminThemeToggle = document.getElementById("adminThemeToggle");
  if (adminThemeToggle) {
    function setAdminTheme(dark) {
      document.body.classList.toggle("dark", dark);
      document.body.dataset.theme = dark ? "dark" : "light";
      adminThemeToggle.textContent = dark ? "☾" : "☼";
      adminThemeToggle.setAttribute("aria-pressed", String(dark));
      adminThemeToggle.style.color = dark ? "#ffffff" : "";
      try {
        localStorage.setItem("admin-portfolio-theme", dark ? "dark" : "light");
      } catch {}
    }

    let savedAdminTheme = null;
    try {
      savedAdminTheme = localStorage.getItem("admin-portfolio-theme");
    } catch {}
    if (savedAdminTheme === "dark") {
      setAdminTheme(true);
    }

    adminThemeToggle.addEventListener("click", () => {
      setAdminTheme(!document.body.classList.contains("dark"));
    });
  }
});

document.addEventListener("toggle", event => {
  if (!(event.target instanceof HTMLDetailsElement) || !event.target.matches(".tool-editor") || !event.target.open) return;
  renderBadgePickers(event.target);
}, true);
