package dev.portfolio.admin;

import dev.portfolio.contact.ContactMessageRepository;
import dev.portfolio.certificate.Certificate;
import dev.portfolio.certificate.CertificateRepository;
import dev.portfolio.content.SiteContent;
import dev.portfolio.content.SiteContentService;
import dev.portfolio.content.ImageStorageService;
import dev.portfolio.education.EducationEntry;
import dev.portfolio.education.EducationRepository;
import dev.portfolio.project.Project;
import dev.portfolio.project.ProjectRepository;
import dev.portfolio.toolbox.ToolboxItem;
import dev.portfolio.toolbox.ToolboxRepository;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
    private final SiteContentService contentService;
    private final ProjectRepository projectRepository;
    private final ContactMessageRepository messageRepository;
    private final ImageStorageService imageStorage;
    private final ToolboxRepository toolboxRepository;
    private final EducationRepository educationRepository;
    private final CertificateRepository certificateRepository;

    public AdminController(
            SiteContentService contentService,
            ProjectRepository projectRepository,
            ContactMessageRepository messageRepository,
            ImageStorageService imageStorage,
            ToolboxRepository toolboxRepository,
            EducationRepository educationRepository,
            CertificateRepository certificateRepository) {
        this.contentService = contentService;
        this.projectRepository = projectRepository;
        this.messageRepository = messageRepository;
        this.imageStorage = imageStorage;
        this.toolboxRepository = toolboxRepository;
        this.educationRepository = educationRepository;
        this.certificateRepository = certificateRepository;
    }

    @InitBinder("content")
    void bindContent(WebDataBinder binder) {
        binder.setAllowedFields(
            "displayName", "heroLine", "heroTagline", "heroDescription",
            "aboutHeadline", "aboutDescription", "aboutExtraDescription",
            "aboutPhotoUrl", "aboutPhotoSecondUrl", "logoUrl", "whatsappUrl", "instagramUrl", "tiktokUrl");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @InitBinder("newProject")
    void bindNewProject(WebDataBinder binder) {
        bindProjectFields(binder);
    }

    @InitBinder("project")
    void bindExistingProject(WebDataBinder binder) {
        bindProjectFields(binder);
    }

    @InitBinder("newTool")
    void bindNewTool(WebDataBinder binder) {
        bindToolFields(binder);
    }

    @InitBinder("tool")
    void bindExistingTool(WebDataBinder binder) {
        bindToolFields(binder);
    }

    @InitBinder("newEducation")
    void bindNewEducation(WebDataBinder binder) {
        bindEducationFields(binder);
    }

    @InitBinder("education")
    void bindExistingEducation(WebDataBinder binder) {
        bindEducationFields(binder);
    }

    @InitBinder("newCertificate")
    void bindNewCertificate(WebDataBinder binder) {
        bindCertificateFields(binder);
    }

    @InitBinder("certificate")
    void bindExistingCertificate(WebDataBinder binder) {
        bindCertificateFields(binder);
    }

    private void bindToolFields(WebDataBinder binder) {
        binder.setAllowedFields("name", "badge", "accentColor");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    private void bindProjectFields(WebDataBinder binder) {
        binder.setAllowedFields(
            "title", "description", "category", "imageUrl", "projectUrl",
            "techStack", "featured", "accentColor");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    private void bindEducationFields(WebDataBinder binder) {
        binder.setAllowedFields(
            "level", "schoolName", "program", "startYear", "endYear", "description", "websiteUrl");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    private void bindCertificateFields(WebDataBinder binder) {
        binder.setAllowedFields(
            "title", "issuer", "issuedYear", "description", "credentialUrl", "imageUrl");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/admin")
    public String dashboard(Model model, CsrfToken csrfToken) {
        return renderDashboard(model, csrfToken, false, "overview");
    }

    @GetMapping("/admin/content")
    public String contentDashboard(Model model, CsrfToken csrfToken) {
        return renderDashboard(model, csrfToken, false, "content");
    }

    @GetMapping("/admin/education")
    public String educationDashboard(Model model, CsrfToken csrfToken) {
        return renderDashboard(model, csrfToken, false, "education");
    }

    @GetMapping("/admin/certificates")
    public String certificatesDashboard(Model model, CsrfToken csrfToken) {
        return renderDashboard(model, csrfToken, false, "certificates");
    }

    @GetMapping("/admin/projects")
    public String projectsDashboard(Model model, CsrfToken csrfToken) {
        return renderDashboard(model, csrfToken, false, "projects");
    }

    @GetMapping("/admin/toolbox")
    public String toolboxDashboard(Model model, CsrfToken csrfToken) {
        return renderDashboard(model, csrfToken, false, "toolbox");
    }

    @GetMapping("/admin/appearance")
    public String appearanceDashboard(Model model, CsrfToken csrfToken) {
        return renderDashboard(model, csrfToken, false, "appearance");
    }

    @GetMapping("/admin/messages")
    public String messagesDashboard(Model model, CsrfToken csrfToken) {
        String view = renderDashboard(model, csrfToken, false, "messages");
        model.addAttribute("messages", messageRepository.findAllByOrderByCreatedAtDesc());
        return view;
    }

    @GetMapping("/admin/preview")
    public String previewDashboard(Model model, CsrfToken csrfToken) {
        return renderDashboard(model, csrfToken, true, "overview");
    }

    private String renderDashboard(Model model, CsrfToken csrfToken, boolean previewMode, String activePage) {
        model.addAttribute("content", contentService.getContent());
        model.addAttribute("projects", projectRepository.findAllByOrderByFeaturedDescIdDesc());
        model.addAttribute("tools", toolboxRepository.findAllByOrderByIdAsc());
        model.addAttribute("educationEntries", educationRepository.findAllByOrderByStartYearAscIdAsc());
        model.addAttribute("certificates", certificateRepository.findAllByOrderByIssuedYearDescIdDesc());
        model.addAttribute("newProject", new Project());
        model.addAttribute("newTool", new ToolboxItem());
        model.addAttribute("newEducation", new EducationEntry());
        model.addAttribute("newCertificate", new Certificate());
        model.addAttribute("projectCount", projectRepository.count());
        model.addAttribute("toolCount", toolboxRepository.count());
        model.addAttribute("educationCount", educationRepository.count());
        model.addAttribute("certificateCount", certificateRepository.count());
        model.addAttribute("messageCount", messageRepository.count());
        model.addAttribute("_csrf", csrfToken);
        model.addAttribute("previewMode", previewMode);
        model.addAttribute("activePage", activePage);
        return "admin";
    }

    @PostMapping("/admin/content")
    public String updateContent(
            @Valid @ModelAttribute("content") SiteContent content,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean clearPhoto,
            @RequestParam(defaultValue = "false") boolean clearSecondPhoto,
            @RequestParam(defaultValue = "false") boolean clearLogo,
            @RequestParam(name = "photo", required = false) MultipartFile photo,
            @RequestParam(name = "secondPhoto", required = false) MultipartFile secondPhoto,
            @RequestParam(name = "logoPhoto", required = false) MultipartFile logoPhoto,
            Model model,
            CsrfToken csrfToken,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateDashboard(model, csrfToken, "content");
            model.addAttribute("notice", "Periksa kembali isian konten yang ditandai.");
            return "admin";
        }
        if (photo != null && !photo.isEmpty()) {
            content.setAboutPhotoUrl(imageStorage.store(photo));
        } else if (clearPhoto) {
            content.setAboutPhotoUrl("");
        }
        if (secondPhoto != null && !secondPhoto.isEmpty()) {
            content.setAboutPhotoSecondUrl(imageStorage.store(secondPhoto));
        } else if (clearSecondPhoto) {
            content.setAboutPhotoSecondUrl("");
        }
        if (logoPhoto != null && !logoPhoto.isEmpty()) {
            content.setLogoUrl(imageStorage.store(logoPhoto));
        } else if (clearLogo) {
            content.setLogoUrl("");
        }
        SiteContent existingContent = contentService.getContent();
        content.setPrimaryColor(existingContent.getPrimaryColor());
        content.setAccentColor(existingContent.getAccentColor());
        content.setBackgroundColor(existingContent.getBackgroundColor());
        content.setAdminLightText(existingContent.getAdminLightText());
        content.setAdminLightSidebar(existingContent.getAdminLightSidebar());
        content.setAdminLightHover(existingContent.getAdminLightHover());
        content.setAdminLightPanel(existingContent.getAdminLightPanel());
        content.setAdminDarkText(existingContent.getAdminDarkText());
        content.setAdminDarkSidebar(existingContent.getAdminDarkSidebar());
        content.setAdminDarkHover(existingContent.getAdminDarkHover());
        content.setAdminDarkPanel(existingContent.getAdminDarkPanel());
        contentService.update(content);
        redirectAttributes.addFlashAttribute("notice", "Konten portfolio berhasil diperbarui.");
        return "redirect:/admin/content";
    }

    @PostMapping("/admin/appearance")
    public String updateAppearance(
            @RequestParam String primaryColor,
            @RequestParam String accentColor,
            @RequestParam String backgroundColor,
            @RequestParam(required = false) String adminLightText,
            @RequestParam(required = false) String adminLightSidebar,
            @RequestParam(required = false) String adminLightHover,
            @RequestParam(required = false) String adminLightPanel,
            @RequestParam(required = false) String adminDarkText,
            @RequestParam(required = false) String adminDarkSidebar,
            @RequestParam(required = false) String adminDarkHover,
            @RequestParam(required = false) String adminDarkPanel,
            RedirectAttributes redirectAttributes) {
        SiteContent content = contentService.getContent();
        adminLightText = useSavedColor(adminLightText, content.getAdminLightText());
        adminLightSidebar = useSavedColor(adminLightSidebar, content.getAdminLightSidebar());
        adminLightHover = useSavedColor(adminLightHover, content.getAdminLightHover());
        adminLightPanel = useSavedColor(adminLightPanel, content.getAdminLightPanel());
        adminDarkText = useSavedColor(adminDarkText, content.getAdminDarkText());
        adminDarkSidebar = useSavedColor(adminDarkSidebar, content.getAdminDarkSidebar());
        adminDarkHover = useSavedColor(adminDarkHover, content.getAdminDarkHover());
        adminDarkPanel = useSavedColor(adminDarkPanel, content.getAdminDarkPanel());
        if (!isValidColor(primaryColor) || !isValidColor(accentColor) || !isValidColor(backgroundColor)
                || !isValidColor(adminLightText) || !isValidColor(adminLightSidebar)
                || !isValidColor(adminLightHover) || !isValidColor(adminLightPanel)
                || !isValidColor(adminDarkText) || !isValidColor(adminDarkSidebar)
                || !isValidColor(adminDarkHover) || !isValidColor(adminDarkPanel)) {
            redirectAttributes.addFlashAttribute("notice", "Pilih warna yang valid untuk setiap bagian.");
            return "redirect:/admin/appearance";
        }
        content.setPrimaryColor(primaryColor);
        content.setAccentColor(accentColor);
        content.setBackgroundColor(backgroundColor);
        content.setAdminLightText(adminLightText);
        content.setAdminLightSidebar(adminLightSidebar);
        content.setAdminLightHover(adminLightHover);
        content.setAdminLightPanel(adminLightPanel);
        content.setAdminDarkText(adminDarkText);
        content.setAdminDarkSidebar(adminDarkSidebar);
        content.setAdminDarkHover(adminDarkHover);
        content.setAdminDarkPanel(adminDarkPanel);
        contentService.update(content);
        redirectAttributes.addFlashAttribute("notice", "Appearance berhasil diperbarui.");
        return "redirect:/admin/appearance";
    }

    @PostMapping("/admin/projects")
    public String createProject(
            @Valid @ModelAttribute("newProject") Project project,
            BindingResult result,
            @RequestParam(name = "photo", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("notice", "Periksa kembali isian project; judul, deskripsi, kategori, dan warna wajib valid.");
            return "redirect:/admin/projects";
        }
        String uploadedImage = imageStorage.store(photo);
        if (uploadedImage != null) {
            project.setImageUrl(uploadedImage);
        }
        projectRepository.save(project);
        redirectAttributes.addFlashAttribute("notice", "Project berhasil ditambahkan.");
        return "redirect:/admin/projects";
    }

    @PostMapping("/admin/projects/{id}")
    public String updateProject(
            @PathVariable Long id,
            @Valid @ModelAttribute("project") Project input,
            BindingResult result,
            @RequestParam(name = "photo", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("notice", "Periksa kembali isian project; judul, deskripsi, kategori, dan warna wajib valid.");
            return "redirect:/admin/projects";
        }
        Project existing = projectRepository.findById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND));
        String uploadedImage = imageStorage.store(photo);
        existing.setTitle(input.getTitle());
        existing.setDescription(input.getDescription());
        existing.setCategory(input.getCategory());
        existing.setImageUrl(uploadedImage == null ? input.getImageUrl() : uploadedImage);
        existing.setProjectUrl(input.getProjectUrl());
        existing.setTechStack(input.getTechStack());
        existing.setFeatured(input.isFeatured());
        existing.setAccentColor(input.getAccentColor());
        projectRepository.save(existing);
        redirectAttributes.addFlashAttribute("notice", "Project berhasil diperbarui.");
        return "redirect:/admin/projects";
    }

    @PostMapping("/admin/toolbox")
    public String createTool(
            @Valid @ModelAttribute("newTool") ToolboxItem tool,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("notice", "Isi nama tool, badge, dan warna dengan benar.");
            return "redirect:/admin/toolbox";
        }
        toolboxRepository.save(tool);
        redirectAttributes.addFlashAttribute("notice", "Tool berhasil ditambahkan ke toolbox.");
        return "redirect:/admin/toolbox";
    }

    @PostMapping("/admin/education")
    public String createEducation(
            @Valid @ModelAttribute("newEducation") EducationEntry entry,
            BindingResult result,
            @RequestParam(name = "photo", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {
        validateEducationYears(entry, result);
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("notice", "Periksa jenjang, nama sekolah, dan rentang tahun pendidikan.");
            return "redirect:/admin/education";
        }
        entry.setImageUrl(imageStorage.store(photo));
        educationRepository.save(entry);
        redirectAttributes.addFlashAttribute("notice", "Riwayat pendidikan berhasil ditambahkan.");
        return "redirect:/admin/education";
    }

    @PostMapping("/admin/education/{id}")
    public String updateEducation(
            @PathVariable Long id,
            @Valid @ModelAttribute("education") EducationEntry input,
            BindingResult result,
            @RequestParam(name = "photo", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {
        validateEducationYears(input, result);
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("notice", "Periksa jenjang, nama sekolah, dan rentang tahun pendidikan.");
            return "redirect:/admin/education";
        }
        EducationEntry existing = educationRepository.findById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND));
        copyEducationFields(input, existing);
        String uploadedImage = imageStorage.store(photo);
        if (uploadedImage != null) {
            existing.setImageUrl(uploadedImage);
        }
        educationRepository.save(existing);
        redirectAttributes.addFlashAttribute("notice", "Riwayat pendidikan berhasil diperbarui.");
        return "redirect:/admin/education";
    }

    @PostMapping("/admin/education/{id}/delete")
    public String deleteEducation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!educationRepository.existsById(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND);
        }
        educationRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("notice", "Riwayat pendidikan berhasil dihapus.");
        return "redirect:/admin/education";
    }

    @PostMapping("/admin/certificates")
    public String createCertificate(
            @Valid @ModelAttribute("newCertificate") Certificate certificate,
            BindingResult result,
            @RequestParam(name = "photo", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("notice", "Periksa kembali judul, penerbit, dan tahun sertifikat.");
            return "redirect:/admin/certificates";
        }
        String uploadedImage = imageStorage.store(photo);
        if (uploadedImage != null) {
            certificate.setImageUrl(uploadedImage);
        }
        certificateRepository.save(certificate);
        redirectAttributes.addFlashAttribute("notice", "Sertifikat berhasil ditambahkan.");
        return "redirect:/admin/certificates";
    }

    @PostMapping("/admin/certificates/{id}")
    public String updateCertificate(
            @PathVariable Long id,
            @Valid @ModelAttribute("certificate") Certificate input,
            BindingResult result,
            @RequestParam(name = "photo", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("notice", "Periksa kembali judul, penerbit, dan tahun sertifikat.");
            return "redirect:/admin/certificates";
        }
        Certificate existing = certificateRepository.findById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND));
        copyCertificateFields(input, existing);
        String uploadedImage = imageStorage.store(photo);
        if (uploadedImage != null) {
            existing.setImageUrl(uploadedImage);
        }
        certificateRepository.save(existing);
        redirectAttributes.addFlashAttribute("notice", "Sertifikat berhasil diperbarui.");
        return "redirect:/admin/certificates";
    }

    @PostMapping("/admin/certificates/{id}/delete")
    public String deleteCertificate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!certificateRepository.existsById(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND);
        }
        certificateRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("notice", "Sertifikat berhasil dihapus.");
        return "redirect:/admin/certificates";
    }

    private void validateEducationYears(EducationEntry entry, BindingResult result) {
        if (entry.getStartYear() != null && entry.getEndYear() != null
                && entry.getEndYear() < entry.getStartYear()) {
            result.rejectValue("endYear", "education.endYear.beforeStartYear",
                "Tahun selesai tidak boleh lebih awal dari tahun mulai.");
        }
    }

    private void copyEducationFields(EducationEntry input, EducationEntry target) {
        target.setLevel(input.getLevel());
        target.setSchoolName(input.getSchoolName());
        target.setProgram(input.getProgram());
        target.setStartYear(input.getStartYear());
        target.setEndYear(input.getEndYear());
        target.setDescription(input.getDescription());
        target.setWebsiteUrl(input.getWebsiteUrl());
    }

    private void copyCertificateFields(Certificate input, Certificate target) {
        target.setTitle(input.getTitle());
        target.setIssuer(input.getIssuer());
        target.setIssuedYear(input.getIssuedYear());
        target.setDescription(input.getDescription());
        target.setCredentialUrl(input.getCredentialUrl());
        target.setImageUrl(input.getImageUrl());
    }

    @PostMapping("/admin/toolbox/{id}")
    public String updateTool(
            @PathVariable Long id,
            @Valid @ModelAttribute("tool") ToolboxItem input,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("notice", "Isi nama tool, badge, dan warna dengan benar.");
            return "redirect:/admin/toolbox";
        }
        ToolboxItem existing = toolboxRepository.findById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND));
        existing.setName(input.getName());
        existing.setBadge(input.getBadge());
        existing.setAccentColor(input.getAccentColor());
        toolboxRepository.save(existing);
        redirectAttributes.addFlashAttribute("notice", "Toolbox berhasil diperbarui.");
        return "redirect:/admin/toolbox";
    }

    @PostMapping("/admin/toolbox/{id}/delete")
    public String deleteTool(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!toolboxRepository.existsById(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND);
        }
        toolboxRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("notice", "Tool berhasil dihapus dari toolbox.");
        return "redirect:/admin/toolbox";
    }

    @PostMapping("/admin/projects/{id}/delete")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!projectRepository.existsById(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND);
        }
        projectRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("notice", "Project berhasil dihapus.");
        return "redirect:/admin/projects";
    }

    @PostMapping("/admin/messages/{id}/delete")
    public String deleteMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!messageRepository.existsById(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND);
        }
        messageRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("notice", "Pesan berhasil dihapus.");
        return "redirect:/admin/messages";
    }

    private boolean isValidColor(String color) {
        return color != null && color.matches("^#[0-9a-fA-F]{6}$");
    }

    private String useSavedColor(String submittedColor, String savedColor) {
        return submittedColor == null ? savedColor : submittedColor;
    }

    private void populateDashboard(Model model, CsrfToken csrfToken, String activePage) {
        model.addAttribute("content", contentService.getContent());
        model.addAttribute("projects", projectRepository.findAllByOrderByFeaturedDescIdDesc());
        model.addAttribute("tools", toolboxRepository.findAllByOrderByIdAsc());
        model.addAttribute("educationEntries", educationRepository.findAllByOrderByStartYearAscIdAsc());
        model.addAttribute("certificates", certificateRepository.findAllByOrderByIssuedYearDescIdDesc());
        model.addAttribute("newProject", new Project());
        model.addAttribute("newTool", new ToolboxItem());
        model.addAttribute("newEducation", new EducationEntry());
        model.addAttribute("newCertificate", new Certificate());
        model.addAttribute("projectCount", projectRepository.count());
        model.addAttribute("toolCount", toolboxRepository.count());
        model.addAttribute("educationCount", educationRepository.count());
        model.addAttribute("certificateCount", certificateRepository.count());
        model.addAttribute("messageCount", messageRepository.count());
        model.addAttribute("_csrf", csrfToken);
        model.addAttribute("activePage", activePage);
        model.addAttribute("previewMode", false);
    }
}
