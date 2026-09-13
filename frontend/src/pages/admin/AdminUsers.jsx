import Card from "../../components/Card";
import AppLayout from "../../layouts/AppLayout";
import { Users } from "lucide-react";

// NOTE: backend gap — there is no /api/admin/users endpoint yet.
// Add this to a new AdminUserController.java:
//   @GetMapping("/api/admin/users")
//   public List<User> getAllUsers() { return userRepository.findAll(); }
// Once that exists, replace this static page with a real
// useEffect(() => api.get("/admin/users")...) call, same pattern as AdminQuestions.jsx.

export default function AdminUsers() {
  return (
    <AppLayout title="User Management">
      <Card className="text-center py-16 max-w-md mx-auto">
        <Users className="w-8 h-8 text-slate-300 mx-auto mb-3" />
        <p className="text-slate-700 font-medium mb-1">Not connected yet</p>
        <p className="text-sm text-slate-500">
          This page needs a <code>GET /api/admin/users</code> endpoint on the
          backend before it can show real user data. Tell me once it's built
          and I'll wire this up.
        </p>
      </Card>
    </AppLayout>
  );
}
