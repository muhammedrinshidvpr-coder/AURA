package aura.service;

import aura.model.Submission;
import aura.model.ResolutionNote;
import aura.model.SubmissionHistory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Simple in-memory implementation to back the UI while persistence is added later.
 */
public class InMemorySubmissionService implements SubmissionService {

    private final List<Submission> store = new ArrayList<>();
    private final List<ResolutionNote> notes = new ArrayList<>();
    private final List<SubmissionHistory> history = new ArrayList<>();
    private final AtomicInteger idGen = new AtomicInteger(100);
    private final AtomicInteger noteIdGen = new AtomicInteger(1);
    private final AtomicInteger histIdGen = new AtomicInteger(1);

    public InMemorySubmissionService() {
        // seed with sample data matching earlier UI examples
        Submission s1 = new Submission(); s1.setSubmissionId(idGen.incrementAndGet()); s1.setTitle("Broken Lamp in Lab"); s1.setStatus("PENDING"); s1.setHypeCount(5);
        Submission s2 = new Submission(); s2.setSubmissionId(idGen.incrementAndGet()); s2.setTitle("WiFi Issues in Block B"); s2.setStatus("IN_PROGRESS"); s2.setHypeCount(12);
        store.add(s1); store.add(s2);
    }

    @Override
    public synchronized Submission createSubmission(Submission s, int studentId) {
        int id = idGen.incrementAndGet();
        s.setSubmissionId(id);
        s.setCreatedAt(LocalDateTime.now());
        s.setStatus(s.getStatus() == null ? "PENDING" : s.getStatus());
        store.add(s);

        // record creation history
        SubmissionHistory h = new SubmissionHistory();
        h.setHistoryId(histIdGen.getAndIncrement());
        h.setSubmissionId(id);
        h.setOldStatus(null);
        h.setNewStatus(s.getStatus());
        h.setChangedBy(studentId);
        h.setChangedAt(LocalDateTime.now());
        history.add(h);

        return s;
    }

    @Override
    public synchronized List<Submission> listAllSubmissions() {
        return new ArrayList<>(store);
    }

    @Override
    public synchronized List<Submission> listSubmissionsByStudent(int studentId) {
        // In-memory model doesn't link submissions to students (anonymity vault). Return all for demo.
        return listAllSubmissions();
    }

    @Override
    public synchronized Submission findById(int submissionId) {
        return store.stream().filter(s -> s.getSubmissionId() == submissionId).findFirst().orElse(null);
    }

    @Override
    public synchronized void updateStatus(int submissionId, String newStatus, int changedBy) {
        Submission s = findById(submissionId);
        if (s == null) return;
        String old = s.getStatus();
        s.setStatus(newStatus);
        SubmissionHistory h = new SubmissionHistory();
        h.setHistoryId(histIdGen.getAndIncrement());
        h.setSubmissionId(submissionId);
        h.setOldStatus(old);
        h.setNewStatus(newStatus);
        h.setChangedBy(changedBy);
        h.setChangedAt(LocalDateTime.now());
        history.add(h);
    }

    @Override
    public synchronized ResolutionNote addResolutionNote(int submissionId, int adminId, String noteText) {
        ResolutionNote rn = new ResolutionNote();
        rn.setNoteId(noteIdGen.getAndIncrement());
        rn.setSubmissionId(submissionId);
        rn.setAdminId(adminId);
        rn.setNote(noteText);
        rn.setCreatedAt(LocalDateTime.now());
        notes.add(rn);
        return rn;
    }

    @Override
    public synchronized List<ResolutionNote> findNotesBySubmission(int submissionId) {
        return notes.stream().filter(n -> n.getSubmissionId() == submissionId).collect(Collectors.toList());
    }

    @Override
    public synchronized List<SubmissionHistory> getHistory(int submissionId) {
        return history.stream().filter(h -> h.getSubmissionId() == submissionId).collect(Collectors.toList());
    }
}
