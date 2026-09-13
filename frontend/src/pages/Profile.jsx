import { useState } from "react";
import AppLayout from "../layouts/AppLayout";
import Card from "../components/Card";
import Avatar from "../components/Avatar";
import Button from "../components/Button";
import { useAuth } from "../context/AuthContext";
import { updateProfile, changePassword } from "../api/users";

function ChangePasswordModal({ onClose }) {
  const [form, setForm] = useState({ current: "", next: "", confirm: "" });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const [saving, setSaving] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setError("");

    if (!form.current || !form.next || !form.confirm) {
      setError("All fields are required.");
      return;
    }
    if (form.next.length < 6) {
      setError("New password must be at least 6 characters.");
      return;
    }
    if (form.next !== form.confirm) {
      setError("New passwords do not match.");
      return;
    }

    setSaving(true);
    try {
      // NOTE: backend gap — PUT /api/users/me/password doesn't exist yet.
      // Add to a UserController:
      //   @PutMapping("/api/users/me/password")
      //   public String changePassword(@RequestBody PasswordChangeRequest req, Authentication auth) { ... }
      // Verify req.currentPassword against the stored hash before saving the new one.
      await changePassword({
        currentPassword: form.current,
        newPassword: form.next,
      });
      setSuccess(true);
    } catch (err) {
      setError(
        err.response?.status === 404
          ? "This feature needs a backend endpoint that isn't built yet (PUT /api/users/me/password)."
          : "Current password is incorrect, or something went wrong."
      );
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <Card className="w-full max-w-sm">
        <h3 className="font-semibold text-slate-900 mb-4">Change Password</h3>

        {success ? (
          <div>
            <p className="text-sm text-green-600 mb-4">Password updated successfully.</p>
            <Button onClick={onClose}>Close</Button>
          </div>
        ) : (
          <form onSubmit={submit} className="space-y-3">
            {error && <p className="text-sm text-red-600">{error}</p>}
            <input
              type="password"
              placeholder="Current Password"
              className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm"
              value={form.current}
              onChange={(e) => setForm({ ...form, current: e.target.value })}
            />
            <input
              type="password"
              placeholder="New Password"
              className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm"
              value={form.next}
              onChange={(e) => setForm({ ...form, next: e.target.value })}
            />
            <input
              type="password"
              placeholder="Confirm New Password"
              className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm"
              value={form.confirm}
              onChange={(e) => setForm({ ...form, confirm: e.target.value })}
            />
            <div className="flex gap-2 justify-end pt-2">
              <Button type="button" variant="secondary" onClick={onClose}>Cancel</Button>
              <Button type="submit" disabled={saving}>{saving ? "Saving..." : "Save"}</Button>
            </div>
          </form>
        )}
      </Card>
    </div>
  );
}

export default function Profile() {
  const { user, logout } = useAuth();
  const [editing, setEditing] = useState(false);
  const [fullName, setFullName] = useState(user?.fullName || "");
  const [saving, setSaving] = useState(false);
  const [status, setStatus] = useState(null); // "success" | "error" | null
  const [showPasswordModal, setShowPasswordModal] = useState(false);

  const saveProfile = async () => {
    setSaving(true);
    setStatus(null);
    try {
      // NOTE: backend gap — PUT /api/users/me doesn't exist yet.
      // Add to UserController: @PutMapping("/api/users/me") updating fullName for the authenticated user.
      await updateProfile({ fullName });
      setStatus("success");
      setEditing(false);
    } catch {
      setStatus("error");
    } finally {
      setSaving(false);
    }
  };

  if (!user) {
    return (
      <AppLayout title="Profile & Settings">
        <p className="text-slate-400">Loading profile...</p>
      </AppLayout>
    );
  }

  return (
    <AppLayout title="Profile & Settings">
      <Card className="max-w-lg">
        <div className="flex items-center gap-4 mb-6">
          <Avatar name={user.fullName} size="lg" />
          <div className="flex-1">
            {editing ? (
              <input
                className="border border-slate-200 rounded-lg px-3 py-1.5 text-sm w-full"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
              />
            ) : (
              <p className="font-semibold text-slate-900">{user.fullName}</p>
            )}
            <p className="text-sm text-slate-500">{user.email}</p>
            <p className="text-xs text-slate-400 mt-0.5">{user.role}</p>
          </div>
        </div>

        {status === "success" && <p className="text-sm text-green-600 mb-3">Profile updated.</p>}
        {status === "error" && (
          <p className="text-sm text-red-600 mb-3">
            Couldn't save — the backend needs a PUT /api/users/me endpoint first.
          </p>
        )}

        <div className="flex gap-2 mb-6">
          {editing ? (
            <>
              <Button onClick={saveProfile} disabled={saving}>
                {saving ? "Saving..." : "Save Changes"}
              </Button>
              <Button variant="secondary" onClick={() => setEditing(false)}>Cancel</Button>
            </>
          ) : (
            <Button variant="secondary" onClick={() => setEditing(true)}>Edit Profile</Button>
          )}
        </div>

        <h4 className="font-medium text-slate-800 mb-2">Security</h4>
        <Button variant="secondary" className="mb-6" onClick={() => setShowPasswordModal(true)}>
          Change Password
        </Button>

        <h4 className="font-medium text-slate-800 mb-2">Account</h4>
        <Button variant="danger" onClick={logout}>Logout</Button>
      </Card>

      {showPasswordModal && (
        <ChangePasswordModal onClose={() => setShowPasswordModal(false)} />
      )}
    </AppLayout>
  );
}
