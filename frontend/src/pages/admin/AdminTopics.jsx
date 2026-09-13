import { useEffect, useState } from "react";
import AppLayout from "../../layouts/AppLayout";
import Card from "../../components/Card";
import Button from "../../components/Button";
import { getAllTopics, createTopic, updateTopic, deleteTopic } from "../../api/questions";
import { Trash2, Pencil, Layers, Plus, X } from "lucide-react";

export default function AdminTopics() {
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  // Create form
  const [newTopicName, setNewTopicName] = useState("");
  const [newTopicDesc, setNewTopicDesc] = useState("");
  const [creating, setCreating] = useState(false);

  // Edit modal
  const [editingTopic, setEditingTopic] = useState(null);
  const [editName, setEditName] = useState("");
  const [editDesc, setEditDesc] = useState("");
  const [updating, setUpdating] = useState(false);
  const [editError, setEditError] = useState("");

  const loadTopics = () => {
    setLoading(true);
    setError("");
    getAllTopics()
      .then((res) => setTopics(res.data || []))
      .catch(() => setError("Could not load topics from server."))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadTopics();
  }, []);

  const handleAddTopic = async (e) => {
    e.preventDefault();
    if (!newTopicName.trim()) return;

    setCreating(true);
    setError("");
    try {
      await createTopic({
        name: newTopicName.trim(),
        description: newTopicDesc.trim() || undefined,
      });
      setNewTopicName("");
      setNewTopicDesc("");
      setSuccessMsg("Topic created successfully.");
      loadTopics();
      setTimeout(() => setSuccessMsg(""), 3000);
    } catch (err) {
      setError(
        err.response?.data?.message || "Failed to create topic. A topic with this name may already exist."
      );
    } finally {
      setCreating(false);
    }
  };

  const openEditModal = (t) => {
    setEditingTopic(t);
    setEditName(t.name);
    setEditDesc(t.description || "");
    setEditError("");
  };

  const closeEditModal = () => {
    setEditingTopic(null);
    setEditError("");
  };

  const handleUpdateTopic = async (e) => {
    e.preventDefault();
    if (!editName.trim()) {
      setEditError("Topic name cannot be empty.");
      return;
    }

    setUpdating(true);
    setEditError("");
    try {
      await updateTopic(editingTopic.id, {
        name: editName.trim(),
        description: editDesc.trim() || undefined,
      });
      setSuccessMsg("Topic updated successfully.");
      closeEditModal();
      loadTopics();
      setTimeout(() => setSuccessMsg(""), 3000);
    } catch (err) {
      setEditError(
        err.response?.data?.message || "Failed to update topic. A topic with this name may already exist."
      );
    } finally {
      setUpdating(false);
    }
  };

  const handleDeleteTopic = async (topic) => {
    if (!confirm(`Are you sure you want to delete topic "${topic.name}"?`)) {
      return;
    }

    setError("");
    try {
      await deleteTopic(topic.id);
      setSuccessMsg(`Topic "${topic.name}" deleted.`);
      loadTopics();
      setTimeout(() => setSuccessMsg(""), 3000);
    } catch (err) {
      setError(
        err.response?.data?.message || "Failed to delete topic. Please verify no questions are associated with it."
      );
    }
  };

  return (
    <AppLayout title="Manage Topics">
      {/* Create Topic Card */}
      <Card className="mb-6 max-w-2xl">
        <h3 className="font-semibold text-slate-900 mb-3 text-sm">Add New Topic</h3>
        <form onSubmit={handleAddTopic} className="space-y-3">
          <div className="flex gap-2">
            <input
              value={newTopicName}
              onChange={(e) => setNewTopicName(e.target.value)}
              placeholder="Topic name (e.g. System Design, Kubernetes)..."
              className="flex-1 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
            <input
              value={newTopicDesc}
              onChange={(e) => setNewTopicDesc(e.target.value)}
              placeholder="Description (optional)..."
              className="flex-1 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <Button type="submit" disabled={creating} className="flex items-center gap-1">
              <Plus className="w-4 h-4" /> {creating ? "Adding..." : "Add Topic"}
            </Button>
          </div>
        </form>
      </Card>

      {successMsg && (
        <div className="bg-green-50 border border-green-200 text-green-700 text-sm px-4 py-2.5 rounded-lg mb-4">
          {successMsg}
        </div>
      )}

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-2.5 rounded-lg mb-4">
          {error}
        </div>
      )}

      {loading && <p className="text-slate-400 py-6">Loading topics...</p>}

      {!loading && topics.length === 0 && (
        <div className="text-center py-16">
          <Layers className="w-8 h-8 text-slate-300 mx-auto mb-2" />
          <p className="text-slate-500">No topics found. Add one above.</p>
        </div>
      )}

      {/* Topics Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {topics.map((t) => (
          <Card key={t.id} className="flex flex-col justify-between hover:border-slate-300 transition-colors">
            <div>
              <div className="flex items-start justify-between mb-1">
                <span className="font-semibold text-slate-800">{t.name}</span>
                <div className="flex gap-1">
                  <button
                    onClick={() => openEditModal(t)}
                    className="p-1.5 text-slate-400 hover:text-blue-600 rounded-md hover:bg-slate-100 cursor-pointer"
                    title="Edit Topic"
                  >
                    <Pencil className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDeleteTopic(t)}
                    className="p-1.5 text-slate-400 hover:text-red-600 rounded-md hover:bg-slate-100 cursor-pointer"
                    title="Delete Topic"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
              {t.description && (
                <p className="text-xs text-slate-500 line-clamp-2">{t.description}</p>
              )}
            </div>
          </Card>
        ))}
      </div>

      {/* Edit Topic Modal */}
      {editingTopic && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <Card className="w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-semibold text-slate-900">Edit Topic</h3>
              <button
                onClick={closeEditModal}
                className="text-slate-400 hover:text-slate-600 cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {editError && (
              <p className="text-sm text-red-600 mb-3 bg-red-50 p-2.5 rounded-lg border border-red-200">
                {editError}
              </p>
            )}

            <form onSubmit={handleUpdateTopic} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">
                  Topic Name *
                </label>
                <input
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">
                  Description
                </label>
                <textarea
                  rows={3}
                  value={editDesc}
                  onChange={(e) => setEditDesc(e.target.value)}
                  className="w-full border border-slate-200 rounded-lg p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <Button type="button" variant="secondary" onClick={closeEditModal}>
                  Cancel
                </Button>
                <Button type="submit" disabled={updating}>
                  {updating ? "Saving..." : "Save Changes"}
                </Button>
              </div>
            </form>
          </Card>
        </div>
      )}
    </AppLayout>
  );
}
