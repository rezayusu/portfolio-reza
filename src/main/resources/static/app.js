const $ = (selector) => document.querySelector(selector);
const projectGrid = $("#projectGrid");
const projectFilterBar = $("#projectFilterBar");
const dialog = $("#projectDialog");
const dialogContent = $("#dialogContent");
let csrfToken = document.querySelector('meta[name="_csrf"]')?.content || "";
let csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || "";
const setHeroMetric = (selector, value) => {
  const metric = $(selector);
  if (metric) metric.textContent = String(value).padStart(2, "0");
};
const aboutPhotoAudio = $("#aboutHoverAudio");
const aboutPhotoPlaceholder = $("#aboutPhoto").closest(".photo-placeholder");
const photoAudioHint = $("#photoAudioHint");

function updateAboutPhotoHint(message = "") {
  const canSwapPhoto = aboutPhotoPlaceholder.classList.contains("has-photo-swap");
  const canPlayAudio = Boolean(aboutPhotoAudio.src);
  photoAudioHint.hidden = !canSwapPhoto && !canPlayAudio && !message;
  photoAudioHint.textContent = message
    ? `${canSwapPhoto ? "Hover/tap: foto · " : ""}${message}`
    : [
      canSwapPhoto ? "Hover/tap: foto 2" : "",
      canPlayAudio ? (canSwapPhoto ? " · klik: musik ♫" : "Hover/klik: musik ♫") : ""
    ].join("");
}

function playAboutPhotoAudio() {
  if (!aboutPhotoAudio.src) return;
  const playback = aboutPhotoAudio.play();
  if (playback) {
    playback.catch(error => {
      if (error.name === "NotAllowedError") {
        updateAboutPhotoHint("Klik foto untuk memutar lagu ♫");
      } else {
        console.error("Could not play the About photo audio.", error);
        updateAboutPhotoHint("Lagu tidak dapat diputar.");
      }
    });
  }
}

function stopAboutPhotoAudio() {
  if (!aboutPhotoAudio.src) return;
  aboutPhotoAudio.pause();
  aboutPhotoAudio.currentTime = 0;
}

function setAboutPhotoSwapped(swapped) {
  if (!aboutPhotoPlaceholder.classList.contains("has-photo-swap")) return;
  aboutPhotoPlaceholder.classList.toggle("is-photo-swapped", swapped);
  aboutPhotoPlaceholder.setAttribute("aria-pressed", String(swapped));
}

aboutPhotoPlaceholder.addEventListener("pointerenter", () => {
  setAboutPhotoSwapped(true);
  playAboutPhotoAudio();
});
aboutPhotoPlaceholder.addEventListener("pointerleave", () => {
  setAboutPhotoSwapped(false);
  stopAboutPhotoAudio();
});
aboutPhotoPlaceholder.addEventListener("click", () => {
  if (window.matchMedia("(hover: none)").matches) {
    setAboutPhotoSwapped(!aboutPhotoPlaceholder.classList.contains("is-photo-swapped"));
  }
  playAboutPhotoAudio();
});
aboutPhotoPlaceholder.addEventListener("focus", () => {
  setAboutPhotoSwapped(true);
  playAboutPhotoAudio();
});
aboutPhotoPlaceholder.addEventListener("blur", () => {
  setAboutPhotoSwapped(false);
  stopAboutPhotoAudio();
});
aboutPhotoPlaceholder.addEventListener("keydown", event => {
  if (event.key === "Enter" || event.key === " ") {
    event.preventDefault();
    setAboutPhotoSwapped(!aboutPhotoPlaceholder.classList.contains("is-photo-swapped"));
    playAboutPhotoAudio();
  }
});

async function refreshCsrfToken() {
  const response = await fetch("/api/csrf", { headers: { "Accept": "application/json" } });
  if (!response.ok) throw new Error("Could not initialize the secure message form.");
  const data = await response.json();
  csrfToken = data.token || "";
  csrfHeader = data.headerName || "";
  if (!csrfToken || !csrfHeader) throw new Error("Could not initialize the secure message form.");
}

$("#year").textContent = new Date().getFullYear();

const menuToggle = $("#menuToggle");
const mainNav = $("#mainNav");
menuToggle.addEventListener("click", () => {
  const opened = mainNav.classList.toggle("open");
  menuToggle.setAttribute("aria-expanded", String(opened));
});
mainNav.querySelectorAll("a").forEach(link => link.addEventListener("click", () => {
  mainNav.classList.remove("open");
  menuToggle.setAttribute("aria-expanded", "false");
}));

const observer = new IntersectionObserver(entries => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add("is-visible");
    } else {
      entry.target.classList.remove("is-visible");
    }
  });
}, { threshold: 0.12 });
function observeMotionElements(root = document) {
  root.querySelectorAll(".reveal, .section-label, .project-card, .education-card, .certificate-card, .tool-sticker").forEach((el, index) => {
    if (el.matches(".project-card, .education-card, .certificate-card, .tool-sticker")) {
      el.style.setProperty("--motion-delay", `${Math.min(index, 5) * 70}ms`);
    }
    observer.observe(el);
  });
}
observeMotionElements();

const parallaxSections = [...document.querySelectorAll(".hero, .section-block")];
if (parallaxSections.length && !window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
  let parallaxFrame = 0;
  const updateParallax = () => {
    parallaxFrame = 0;
    parallaxSections.forEach(section => {
      const bounds = section.getBoundingClientRect();
      if (section.matches(".hero")) {
        const scrollOffset = Math.max(0, Math.min(section.offsetHeight, -bounds.top));
        section.style.setProperty("--hero-scroll", `${scrollOffset}px`);
        section.style.setProperty("--hero-background-shift", `${scrollOffset * 0.5}px`);
      } else {
        const sectionOffset = Math.max(
          -260,
          Math.min(260, bounds.top + bounds.height / 2 - window.innerHeight / 2)
        );
        section.style.setProperty("--section-parallax", `${sectionOffset}px`);
        section.style.setProperty("--section-background-shift", `${sectionOffset * -0.7}px`);
      }
    });
  };
  window.addEventListener("scroll", () => {
    if (!parallaxFrame) parallaxFrame = window.requestAnimationFrame(updateParallax);
  }, { passive: true });
  window.addEventListener("resize", updateParallax, { passive: true });
  updateParallax();
}

function isVideoUrl(url = "") {
  return /\.(mp4|webm|mov)(\?.*)?$/i.test(url);
}
function escapeHtml(value = "") {
  return String(value).replace(/[&<>"']/g, char => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
  })[char]);
}
function safeUrl(value) {
  try {
    const url = new URL(value, window.location.origin);
    return ["http:", "https:"].includes(url.protocol) ? url.href : "";
  } catch { return ""; }
}
function setFooterSocialLink(selector, value) {
  const link = $(selector);
  const url = safeUrl(value || "");
  if (!link || !url) return;
  link.href = url;
  link.target = "_blank";
  link.rel = "noopener noreferrer";
  link.hidden = false;
}
function renderProject(project, index) {
  const galleryMedia = (project.galleryUrls || "").split(/[,;\n]+/).map(url => safeUrl(url.trim())).filter(Boolean);
  const primaryMedia = safeUrl(project.imageUrl || "");
  const image = primaryMedia && !isVideoUrl(primaryMedia)
    ? primaryMedia
    : galleryMedia.find(url => !isVideoUrl(url)) || "";
  const galleryVideo = galleryMedia.find(isVideoUrl) || "";
  const accentColor = /^#[0-9a-fA-F]{6}$/.test(project.accentColor || "") ? project.accentColor : "#f4d66d";
  const tech = (project.techStack || "").split(",").map(x => x.trim()).filter(Boolean).slice(0, 4);
  const field = ["DKV", "RPL"].includes(project.field) ? project.field : "";
  const preview = `${image ? `<img loading="lazy" src="${escapeHtml(image)}" alt="${escapeHtml(project.title)} project preview">` : ""}${galleryVideo ? `<video class="project-preview-video" src="${escapeHtml(galleryVideo)}" muted loop playsinline preload="none" aria-hidden="true"></video>` : ""}`;
  return `<button class="project-card" type="button" data-project-id="${Number(project.id)}" data-project-field="${field}" style="--project-accent:${accentColor}" aria-label="View ${escapeHtml(project.title)} details">
    <div class="project-image">${preview}
      <span class="project-number">NO. 0${index + 1}</span></div>
    <div class="project-content"><div class="project-labels">${field ? `<span class="project-field">${field}</span>` : ""}<span class="project-category">${escapeHtml(project.category)}</span></div>
      <h3>${escapeHtml(project.title)}</h3><p>${escapeHtml(project.description)}</p>
      <div class="tech-tags">${tech.map(item => `<span>${escapeHtml(item)}</span>`).join("")}</div></div></button>`;
}

function playProjectPreview(card) {
  const video = card.querySelector(".project-preview-video");
  if (!video) return;
  video.currentTime = 0;
  card.classList.add("is-preview-playing");
  video.play().catch(error => {
    card.classList.remove("is-preview-playing");
    if (error.name !== "AbortError") {
      console.error("Could not play the project preview video.", error);
    }
  });
}

function stopProjectPreview(card) {
  const video = card.querySelector(".project-preview-video");
  if (!video) return;
  card.classList.remove("is-preview-playing");
  video.pause();
  video.currentTime = 0;
}

let projectsCache = [];
let educationCache = [];
let activeProjectFilter = "";

function renderProjectFilters() {
  const filters = [
    ["DKV", "DKV", projectsCache.filter(project => project.field === "DKV").length],
    ["RPL", "RPL", projectsCache.filter(project => project.field === "RPL").length]
  ];
  if (!activeProjectFilter) {
    activeProjectFilter = filters.find(([, , count]) => count > 0)?.[0] || filters[0][0];
  }
  projectFilterBar.innerHTML = filters.map(([value, label, count]) =>
    `<button type="button" data-project-filter="${value}" class="${value === activeProjectFilter ? "is-active" : ""}" aria-pressed="${value === activeProjectFilter}">${label}<span class="filter-count">${count}</span></button>`
  ).join("");
}

function updateProjectFilterResults() {
  const cards = [...projectGrid.querySelectorAll(".project-card")];
  const visibleCount = cards.filter(card => card.dataset.projectField === activeProjectFilter).length;
  cards.forEach(card => {
    const matches = Boolean(activeProjectFilter) && card.dataset.projectField === activeProjectFilter;
    card.hidden = !matches;
    if (!matches) stopProjectPreview(card);
  });

  const prompt = projectGrid.querySelector(".project-filter-prompt");
  if (prompt) {
    prompt.hidden = visibleCount > 0;
    prompt.textContent = activeProjectFilter
      ? `Belum ada project untuk bidang ${activeProjectFilter}.`
      : "Pilih bidang DKV atau RPL untuk melihat project.";
  }
}

projectFilterBar.addEventListener("click", event => {
  const button = event.target.closest("[data-project-filter]");
  if (!button) return;
  activeProjectFilter = button.dataset.projectFilter;
  projectFilterBar.querySelectorAll("[data-project-filter]").forEach(filter => {
    const active = filter === button;
    filter.classList.toggle("is-active", active);
    filter.setAttribute("aria-pressed", String(active));
  });
  updateProjectFilterResults();
});

async function loadSiteContent() {
  try {
    const response = await fetch("/api/site-content", { headers: { "Accept": "application/json" } });
    if (!response.ok) throw new Error("Could not load portfolio content");
    const content = await response.json();
    const logoUrl = safeUrl(content.logoUrl || "");
    document.querySelectorAll("[data-logo-mark]").forEach(brandMark => {
      if (!logoUrl) return;
      const logo = document.createElement("img");
      logo.className = "brand-mark brand-logo-image";
      logo.src = logoUrl;
      logo.alt = `${content.displayName} logo`;
      logo.id = brandMark.id;
      logo.dataset.logoMark = "";
      brandMark.replaceWith(logo);
    });
    $("#brandName").textContent = content.displayName;
    $("#displayName").textContent = content.displayName;
    const codeName = $("#codeName");
    if (codeName) codeName.textContent = `'${content.displayName}'`;
    $("#photoInitial").textContent = content.displayName.trim().charAt(0).toUpperCase();
    $("#footerName").textContent = content.displayName;
    $("#heroLine").textContent = content.heroLine;
    $("#heroTagline").textContent = content.heroTagline;
    $("#heroDescription").textContent = content.heroDescription;
    $("#aboutHeadline").textContent = content.aboutHeadline;
    $("#aboutDescription").textContent = content.aboutDescription;
    $("#aboutExtraDescription").textContent = content.aboutExtraDescription;
    setFooterSocialLink("#whatsappLink", content.whatsappUrl);
    setFooterSocialLink("#instagramLink", content.instagramUrl);
    setFooterSocialLink("#githubLink", content.githubUrl);
    const photoUrl = safeUrl(content.aboutPhotoUrl || "");
    const secondPhotoUrl = safeUrl(content.aboutPhotoSecondUrl || "");
    const firstPhoto = $("#aboutPhoto");
    const secondPhoto = $("#aboutPhotoSecond");
    const hoverAudioUrl = safeUrl(content.aboutHoverAudioUrl || "");
    const photoPlaceholder = firstPhoto.closest(".photo-placeholder");
    const updatePhotoInteraction = () => {
      const canSwapPhoto = Boolean(photoUrl && secondPhotoUrl && photoPlaceholder.classList.contains("has-photo-swap"));
      const canPlayAudio = Boolean(hoverAudioUrl);
      photoPlaceholder.classList.remove("is-photo-swapped");
      if (canSwapPhoto || canPlayAudio) {
        photoPlaceholder.tabIndex = 0;
        photoPlaceholder.setAttribute("role", "button");
        photoPlaceholder.setAttribute("aria-label", [
          canSwapPhoto ? "Arahkan kursor atau fokus untuk mengganti foto." : "",
          canPlayAudio ? "Klik atau arahkan kursor untuk memutar lagu." : ""
        ].filter(Boolean).join(" "));
        photoPlaceholder.setAttribute("aria-pressed", "false");
      } else {
        photoPlaceholder.removeAttribute("tabindex");
        photoPlaceholder.removeAttribute("role");
        photoPlaceholder.removeAttribute("aria-label");
        photoPlaceholder.removeAttribute("aria-pressed");
      }
      updateAboutPhotoHint();
    };
    firstPhoto.onerror = () => {
      firstPhoto.style.display = "none";
      $("#photoInitial").style.display = "";
      photoPlaceholder.classList.remove("has-photo-swap");
      updatePhotoInteraction();
    };
    secondPhoto.onerror = () => {
      secondPhoto.style.display = "none";
      photoPlaceholder.classList.remove("has-photo-swap");
      updatePhotoInteraction();
    };
    secondPhoto.onload = () => {
      if (photoUrl && secondPhotoUrl) photoPlaceholder.classList.add("has-photo-swap");
      updatePhotoInteraction();
    };
    if (hoverAudioUrl) {
      aboutPhotoAudio.src = hoverAudioUrl;
    } else {
      aboutPhotoAudio.removeAttribute("src");
    }
    photoPlaceholder.classList.remove("has-photo-swap", "is-photo-swapped");
    const visiblePhotoUrl = photoUrl || secondPhotoUrl;
    if (visiblePhotoUrl) {
      firstPhoto.src = visiblePhotoUrl;
      firstPhoto.style.display = "block";
      $("#photoInitial").style.display = "none";
    } else {
      firstPhoto.removeAttribute("src");
      firstPhoto.style.display = "none";
      $("#photoInitial").style.display = "";
    }
    if (photoUrl && secondPhotoUrl) {
      secondPhoto.src = secondPhotoUrl;
      secondPhoto.style.display = "block";
      if (secondPhoto.complete && secondPhoto.naturalWidth > 0) {
        photoPlaceholder.classList.add("has-photo-swap");
      }
    } else {
      secondPhoto.removeAttribute("src");
      secondPhoto.style.display = "none";
    }
    updatePhotoInteraction();
  } catch (error) {
    console.error("Could not load saved portfolio content.", error);
  }
}

async function loadToolbox() {
  const toolboxGrid = $("#toolboxGrid");
  try {
    const response = await fetch("/api/toolbox", { headers: { "Accept": "application/json" } });
    if (!response.ok) throw new Error("Could not load toolbox");
    const tools = await response.json();
    setHeroMetric("#heroToolCount", tools.length);
    if (!tools.length) {
      toolboxGrid.innerHTML = '<p class="loading-note">Belum ada tools yang ditambahkan nih.</p>';
      return;
    }
    toolboxGrid.innerHTML = tools.map(tool => {
      const color = /^#[0-9a-fA-F]{6}$/.test(tool.accentColor || "") ? tool.accentColor : "#f4d66d";
      const icon = safeUrl(tool.badgeImageUrl || "");
      const link = safeUrl(tool.badgeLink || "");
      const mark = icon
        ? `<span class="tool-mark tool-icon"><img loading="lazy" src="${escapeHtml(icon)}" alt="${escapeHtml(tool.name)} icon"></span>`
        : `<b class="tool-mark">${escapeHtml(tool.badge || "★")}</b>`;
      const sticker = `${mark}<span class="tool-name">${escapeHtml(tool.name)}</span>`;
      if (link) {
        return `<a class="tool-sticker is-linked" style="--tool-color:${color}" href="${escapeHtml(link)}" target="_blank" rel="noopener noreferrer">${sticker}</a>`;
      }
      return `<span class="tool-sticker" style="--tool-color:${color}">${sticker}</span>`;
    }).join("");
    observeMotionElements(toolboxGrid);
  } catch (error) {
    console.error("Could not load saved toolbox items.", error);
    toolboxGrid.innerHTML = '<p class="loading-note">Could not load toolbox items.</p>';
  }
}

async function loadEducation() {
  const timeline = $("#educationTimeline");
  try {
    const response = await fetch("/api/education", { headers: { "Accept": "application/json" } });
    if (!response.ok) throw new Error("Could not load education history");
    educationCache = await response.json();
    if (!educationCache.length) {
      timeline.innerHTML = '<p class="loading-note">Riwayat pendidikan akan segera ditambahkan.</p>';
      return;
    }
    timeline.innerHTML = educationCache.map((entry, index) => {
      const period = `${entry.startYear} — ${entry.endYear || "Now"}`;
      const image = safeUrl(entry.imageUrl || "");
      return `<button class="education-card" type="button" data-education-index="${index}" aria-label="View details for ${escapeHtml(entry.schoolName)}"><span class="education-level">${escapeHtml(entry.level)}</span><span class="education-copy"><span class="education-period">${escapeHtml(period)}</span><span class="education-school-name">${escapeHtml(entry.schoolName)}</span>${image ? `<span class="education-photo-frame"><img class="education-photo" loading="lazy" src="${escapeHtml(image)}" alt=""></span>` : ""}${entry.program ? `<span class="education-program">${escapeHtml(entry.program)}</span>` : ""}${entry.description ? `<span class="education-description">${escapeHtml(entry.description)}</span>` : ""}</span></button>`;
    }).join("");
    observeMotionElements(timeline);
    timeline.querySelectorAll(".education-card").forEach(card => {
      card.addEventListener("click", () => {
        const entry = educationCache[Number(card.dataset.educationIndex)];
        if (!entry) return;
        const period = `${entry.startYear} — ${entry.endYear || "Now"}`;
        const image = safeUrl(entry.imageUrl || "");
        const website = safeUrl(entry.websiteUrl || "");
        dialogContent.innerHTML = `
          <span class="project-category">${escapeHtml(entry.level)} · ${escapeHtml(period)}</span>
          <h2 class="dialog-title">${escapeHtml(entry.schoolName)}</h2>
          ${image ? `<img class="dialog-image" src="${escapeHtml(image)}" alt="${escapeHtml(`Dokumentasi ${entry.schoolName}`)}">` : ""}
          ${entry.program ? `<p class="education-dialog-program">${escapeHtml(entry.program)}</p>` : ""}
          ${entry.description ? `<p class="dialog-description">${escapeHtml(entry.description)}</p>` : ""}
          ${website ? `<a class="dialog-link" href="${escapeHtml(website)}" target="_blank" rel="noopener noreferrer">Kunjungi website ↗</a>` : ""}`;
        dialog.showModal();
      });
    });
  } catch (error) {
    console.error("Could not load education history.", error);
    timeline.innerHTML = '<p class="loading-note">Could not load education history.</p>';
  }
}

async function loadCertificates() {
  const certificateGrid = $("#certificateGrid");
  try {
    const response = await fetch("/api/certificates", { headers: { "Accept": "application/json" } });
    if (!response.ok) throw new Error("Could not load certificates");
    const certificates = await response.json();
    setHeroMetric("#heroCertificateCount", certificates.length);
    if (!certificates.length) {
      certificateGrid.innerHTML = '<p class="loading-note">Sertifikat akan segera ditambahkan.</p>';
      return;
    }
    certificateGrid.innerHTML = certificates.map((certificate, index) => {
      const image = safeUrl(certificate.imageUrl || "");
      return `<article class="certificate-card" role="button" tabindex="0" data-certificate-index="${index}" aria-label="View details for ${escapeHtml(certificate.title)}">
        <span class="certificate-image-frame">${image ? `<img class="certificate-image" loading="lazy" src="${escapeHtml(image)}" alt="">` : '<span class="certificate-stamp" aria-hidden="true">✧</span>'}</span>
        <div class="certificate-copy">
          <span class="certificate-year">${escapeHtml(certificate.issuedYear)}</span>
          <h3>${escapeHtml(certificate.title)}</h3>
          <p class="certificate-issuer">${escapeHtml(certificate.issuer)}</p>
        </div>
      </article>`;
    }).join("");
    observeMotionElements(certificateGrid);
    certificateGrid.querySelectorAll(".certificate-card").forEach(card => {
      const showCertificateDetails = () => {
        const certificate = certificates[Number(card.dataset.certificateIndex)];
        if (!certificate) return;
        const image = safeUrl(certificate.imageUrl || "");
        const credential = safeUrl(certificate.credentialUrl || "");
        dialogContent.innerHTML = `
          <span class="project-category">CERTIFICATE · ${escapeHtml(certificate.issuedYear)}</span>
          <h2 class="dialog-title">${escapeHtml(certificate.title)}</h2>
          ${image ? `<img class="certificate-dialog-image" src="${escapeHtml(image)}" alt="${escapeHtml(certificate.title)} certificate">` : ""}
          <p class="certificate-dialog-issuer">${escapeHtml(certificate.issuer)}</p>
          ${certificate.description ? `<p class="dialog-description">${escapeHtml(certificate.description)}</p>` : ""}
          ${credential ? `<a class="dialog-link" href="${escapeHtml(credential)}" target="_blank" rel="noopener noreferrer">Lihat sertifikat ↗</a>` : ""}`;
        dialog.showModal();
      };
      card.addEventListener("click", showCertificateDetails);
      card.addEventListener("keydown", event => {
        if (event.key === "Enter" || event.key === " ") {
          event.preventDefault();
          showCertificateDetails();
        }
      });
    });
  } catch (error) {
    console.error("Could not load certificates.", error);
    certificateGrid.innerHTML = '<p class="loading-note">Could not load certificates.</p>';
  }
}

async function loadProjects() {
  try {
    const response = await fetch("/api/projects", { headers: { "Accept": "application/json" } });
    if (!response.ok) throw new Error("Could not load projects");
    projectsCache = await response.json();
    setHeroMetric("#heroProjectCount", projectsCache.length);
    renderProjectFilters();
    if (!projectsCache.length) {
      projectGrid.innerHTML = '<p class="loading-note">Belum ada project nih — yuk tambahin project pertama!</p>';
      return;
    }
    projectGrid.innerHTML = `${projectsCache.map(renderProject).join("")}<p class="project-filter-prompt" role="status">Pilih bidang DKV atau RPL untuk melihat project.</p>`;
    updateProjectFilterResults();
    observeMotionElements(projectGrid);
    projectGrid.querySelectorAll(".project-card").forEach(card => {
      card.addEventListener("pointerenter", event => {
        if (event.pointerType !== "touch") playProjectPreview(card);
      });
      card.addEventListener("pointerleave", () => stopProjectPreview(card));
      card.addEventListener("focus", () => playProjectPreview(card));
      card.addEventListener("blur", () => stopProjectPreview(card));
      card.addEventListener("click", () => {
        stopProjectPreview(card);
        const project = projectsCache.find(item => item.id === Number(card.dataset.projectId));
        if (!project) return;
        const mainImage = safeUrl(project.imageUrl || "");
        const rawGallery = (project.galleryUrls || "").split(/[,;\n]+/).map(s => safeUrl(s.trim())).filter(Boolean);
        const mediaItems = [mainImage, ...rawGallery].filter((m, idx, arr) => m && arr.indexOf(m) === idx);
        const link = project.projectUrl && project.projectUrl.trim() !== "" ? safeUrl(project.projectUrl) : "";

        const renderActiveMedia = (url) => {
          if (!url) return '';
          if (isVideoUrl(url)) {
            return `<video id="dialogActiveShot" class="dialog-image" src="${escapeHtml(url)}" controls autoplay muted loop playsinline></video>`;
          }
          return `<img id="dialogActiveShot" class="dialog-image" src="${escapeHtml(url)}" alt="${escapeHtml(project.title)} preview">`;
        };

        dialogContent.innerHTML = `
          <span class="project-category">${escapeHtml(project.category)}</span>
          <h2 class="dialog-title">${escapeHtml(project.title)}</h2>
          <div class="dialog-gallery-container">
            <div class="dialog-active-shot-wrap">
              ${renderActiveMedia(mediaItems[0])}
            </div>
            ${mediaItems.length > 1 ? `
              <div class="dialog-thumbs" aria-label="Media gallery">
                ${mediaItems.map((m, i) => `
                  <button type="button" class="dialog-thumb-btn ${i === 0 ? 'is-active' : ''}" data-media-src="${escapeHtml(m)}" data-is-video="${isVideoUrl(m)}">
                    ${isVideoUrl(m) ? '<div class="dialog-thumb-video-badge">▶ Video</div>' : `<img src="${escapeHtml(m)}" alt="Media ${i+1}" loading="lazy">`}
                  </button>
                `).join("")}
              </div>
            ` : ''}
          </div>
          <p class="dialog-description">${escapeHtml(project.description)}</p>
          <p class="tech-tags">${(project.techStack || "").split(",").map(t => `<span>${escapeHtml(t.trim())}</span>`).join("")}</p>
          ${link ? `<a class="dialog-link" href="${escapeHtml(link)}" target="_blank" rel="noopener noreferrer">Kunjungi project ↗</a>` : ""}`;

        const activeWrap = dialogContent.querySelector(".dialog-active-shot-wrap");
        dialogContent.querySelectorAll(".dialog-thumb-btn").forEach(btn => {
          btn.addEventListener("click", () => {
            dialogContent.querySelectorAll(".dialog-thumb-btn").forEach(b => b.classList.remove("is-active"));
            btn.classList.add("is-active");
            const src = btn.dataset.mediaSrc;
            const isVid = btn.dataset.isVideo === "true";
            if (activeWrap) {
              if (isVid) {
                activeWrap.innerHTML = `<video id="dialogActiveShot" class="dialog-image" src="${escapeHtml(src)}" controls autoplay muted loop playsinline></video>`;
              } else {
                activeWrap.innerHTML = `<img id="dialogActiveShot" class="dialog-image" src="${escapeHtml(src)}" alt="${escapeHtml(project.title)} preview">`;
              }
            }
          });
        });

        dialog.showModal();
      });
    });
  } catch (error) {
    projectGrid.innerHTML = '<p class="loading-note">Gagal memuat project. Pastikan Spring Boot dan PostgreSQL jalan ya, Bang.</p>';
  }
}
$("#closeDialog").addEventListener("click", () => dialog.close());
dialog.addEventListener("click", event => {
  if (event.target === dialog) dialog.close();
});

const contactForm = $("#contactForm");
contactForm?.addEventListener("submit", async event => {
  event.preventDefault();
  const form = event.currentTarget;
  const status = $("#formStatus");
  const submit = form.querySelector("button[type=submit]");
  status.textContent = "Lagi ngirim pesan...";
  submit.disabled = true;
  const payload = {
    name: $("#name").value.trim(),
    email: $("#email").value.trim(),
    message: $("#message").value.trim()
  };
  try {
    if (!csrfToken || !csrfHeader) await refreshCsrfToken();
    const headers = { "Content-Type": "application/json", "Accept": "application/json" };
    if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;
    const response = await fetch("/api/contact", {
      method: "POST", headers, body: JSON.stringify(payload)
    });
    const result = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(result.message || "Message could not be sent. Please try again.");
    status.textContent = result.message || "Message sent. Thank you!";
    form.reset();
  } catch (error) {
    status.textContent = error.message || "Something went wrong. Please try again.";
  } finally {
    submit.disabled = false;
  }
});
loadSiteContent();
refreshCsrfToken().catch(error => console.error("Could not initialize the secure message form.", error));
loadProjects();
loadToolbox();
loadEducation();
loadCertificates();
