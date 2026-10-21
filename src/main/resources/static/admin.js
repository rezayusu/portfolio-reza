const adminThemeToggle = document.querySelector("#adminThemeToggle");
const adminThemeStorageKey = "portfolio-admin-theme";
const adminConfirmDialog = document.querySelector("#adminConfirmDialog");
const adminConfirmTitle = document.querySelector("#adminConfirmTitle");
const adminConfirmDescription = document.querySelector("#adminConfirmDescription");
const adminConfirmCancel = document.querySelector("#adminConfirmCancel");
const adminConfirmDelete = document.querySelector("#adminConfirmDelete");
let adminNoticeTimer;
let adminNavigationController;
let pendingDeleteForm;
let pendingDeleteButton;
let confirmedDeleteButton;

function readSavedTheme() {
  try {
    return localStorage.getItem(adminThemeStorageKey);
  } catch {
    return null;
  }
}

function applyAdminTheme(dark) {
  document.body.classList.toggle("dark", dark);
  document.body.dataset.theme = dark ? "dark" : "light";
  if (adminThemeToggle) {
    adminThemeToggle.textContent = dark ? "☾" : "☼";
    adminThemeToggle.classList.toggle("is-dark-mode", dark);
    adminThemeToggle.setAttribute("aria-pressed", String(dark));
    adminThemeToggle.setAttribute("aria-label", dark ? "Switch to light mode" : "Switch to dark mode");
  }
  try {
    localStorage.setItem(adminThemeStorageKey, dark ? "dark" : "light");
  } catch {
    // Keep the selected theme for this page even when browser storage is unavailable.
  }
}

function initializeAdminPage() {
  if (document.body.dataset.previewMode === "true") {
    document.querySelectorAll(".admin-content form").forEach(form => {
      form.querySelectorAll("input, textarea, select, button").forEach(control => {
        control.disabled = true;
      });
      form.addEventListener("submit", event => event.preventDefault());
    });
  }

  window.clearTimeout(adminNoticeTimer);
  const adminNotice = document.querySelector(".admin-notice");
  if (adminNotice) {
    adminNoticeTimer = window.setTimeout(() => adminNotice.classList.add("notice-dismissed"), 5000);
  }
}

function replaceOptionalElement(current, incoming) {
  if (current && incoming) {
    current.replaceWith(incoming);
  } else if (current) {
    current.remove();
  } else if (incoming) {
    document.querySelector(".admin-main").prepend(incoming);
  }
}

async function navigateAdmin(url, addHistoryEntry) {
  adminNavigationController?.abort();
  const controller = new AbortController();
  adminNavigationController = controller;

  const currentLayout = document.querySelector(".admin-layout");
  currentLayout?.classList.add("is-navigating");

  try {
    const response = await fetch(url, {
      credentials: "same-origin",
      headers: { "X-Requested-With": "XMLHttpRequest" },
      signal: controller.signal
    });
    if (!response.ok) {
      throw new Error(`Admin navigation failed with status ${response.status}`);
    }

    const documentText = await response.text();
    const nextDocument = new DOMParser().parseFromString(documentText, "text/html");
    const nextLayout = nextDocument.querySelector(".admin-layout");
    if (!nextLayout) {
      window.location.assign(url);
      return;
    }

    nextLayout.classList.add("admin-layout-arrive");
    currentLayout?.replaceWith(nextLayout);
    replaceOptionalElement(
      document.querySelector(".admin-notice"),
      nextDocument.querySelector(".admin-notice")
    );
    replaceOptionalElement(
      document.querySelector(".preview-banner"),
      nextDocument.querySelector(".preview-banner")
    );
    document.body.dataset.previewMode = nextDocument.body.dataset.previewMode ?? "false";
    document.title = nextDocument.title;

    if (addHistoryEntry) {
      window.history.pushState({ adminNavigation: true }, "", url);
    }
    initializeAdminPage();
    window.scrollTo(0, 0);
  } catch (error) {
    if (error instanceof DOMException && error.name === "AbortError") {
      return;
    }
    console.error("Could not load the admin page without a full navigation.", error);
    window.location.assign(url);
  }
}

applyAdminTheme(readSavedTheme() === "dark");
initializeAdminPage();
adminThemeToggle?.addEventListener("click", () => {
  applyAdminTheme(document.body.dataset.theme !== "dark");
});

document.addEventListener("click", event => {
  if (!(event.target instanceof Element)) return;

  const link = event.target.closest(".sidebar-nav a[href]");
  if (!link || event.defaultPrevented || event.button !== 0 ||
      event.metaKey || event.ctrlKey || event.shiftKey || event.altKey ||
      link.hasAttribute("target") || link.hasAttribute("download")) {
    return;
  }

  const url = new URL(link.href, window.location.href);
  if (url.origin !== window.location.origin) return;
  event.preventDefault();
  void navigateAdmin(url.href, true);
});

document.addEventListener("submit", event => {
  const deleteButton = event.submitter;
  if (!(deleteButton instanceof HTMLButtonElement) || !deleteButton.matches(".button-delete")) return;
  if (confirmedDeleteButton === deleteButton) {
    confirmedDeleteButton = undefined;
    return;
  }

  event.preventDefault();
  pendingDeleteForm = deleteButton.form;
  pendingDeleteButton = deleteButton;
  if (deleteButton.closest(".education-editor")) {
    adminConfirmTitle.textContent = "Hapus riwayat pendidikan?";
  } else if (deleteButton.closest(".tool-editor")) {
    adminConfirmTitle.textContent = "Hapus tool ini?";
  } else if (deleteButton.closest(".message-details")) {
    adminConfirmTitle.textContent = "Hapus pesan ini?";
  } else {
    adminConfirmTitle.textContent = "Hapus project ini?";
  }
  adminConfirmDescription.textContent = "Item yang dihapus tidak dapat dikembalikan.";
  adminConfirmDialog.showModal();
});

adminConfirmCancel?.addEventListener("click", () => adminConfirmDialog.close());
adminConfirmDelete?.addEventListener("click", () => {
  if (!pendingDeleteForm || !pendingDeleteButton) return;

  const form = pendingDeleteForm;
  const button = pendingDeleteButton;
  pendingDeleteForm = undefined;
  pendingDeleteButton = undefined;
  confirmedDeleteButton = button;
  adminConfirmDialog.close();
  form.requestSubmit(button);
  if (confirmedDeleteButton === button) confirmedDeleteButton = undefined;
});
adminConfirmDialog?.addEventListener("close", () => {
  if (pendingDeleteButton?.isConnected) pendingDeleteButton.focus();
  pendingDeleteForm = undefined;
  pendingDeleteButton = undefined;
});

window.addEventListener("popstate", () => {
  void navigateAdmin(window.location.href, false);
});

document.addEventListener("change", event => {
  if (!(event.target instanceof HTMLInputElement) ||
      !event.target.matches('input[type="file"][name="photo"], input[type="file"][name="secondPhoto"], input[type="file"][name="logoPhoto"]')) {
    return;
  }

  const input = event.target;
  const file = input.files?.[0];
  if (!file) return;
  const label = input.closest("label");
  const uploadControls = input.closest(".upload-controls");
  if (uploadControls) {
    const photoFrame = uploadControls.closest(".upload-row")?.querySelector(".upload-preview");
    if (!photoFrame) return;
    let preview = photoFrame.querySelector("img");
    if (!preview) {
      preview = document.createElement("img");
      preview.alt = input.name === "logoPhoto" ? "Selected navigation logo preview" : input.name === "secondPhoto" ? "Selected second profile photo preview" : "Selected profile photo preview";
      photoFrame.replaceChildren(preview);
    }
    preview.src = URL.createObjectURL(file);
    return;
  }

  if (!label) return;
  let preview = label.querySelector(".selected-image-preview");
  if (!preview) {
    preview = document.createElement("img");
    preview.className = "selected-image-preview";
    preview.alt = "Selected image preview";
    label.append(preview);
  }
  preview.src = URL.createObjectURL(file);
});
