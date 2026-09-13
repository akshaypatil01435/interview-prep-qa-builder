import { useState } from "react";
import { useNavigate, Link, useSearchParams } from "react-router-dom";
import { loginUser } from "../api/auth";
import { useAuth } from "../context/AuthContext";
import Button from "../components/Button";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const expired = searchParams.get("expired") === "1";

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await loginUser(form);
      login(res.data.token, res.data.user);
      navigate("/dashboard");
    } catch (err) {
      setError(
        err.response?.status === 404 || !err.response
          ? "Couldn't reach the server. Is the backend running?"
          : "Invalid email or password"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:flex w-1/2 bg-slate-900 text-white flex-col justify-center px-16">
        <h1 className="text-3xl font-bold mb-2">Interview Prep Q&A Builder</h1>
        <p className="text-slate-400">Prepare smarter. Practice better. Get interview ready.</p>
      </div>

      <div className="flex-1 flex items-center justify-center px-6">
        <form onSubmit={handleSubmit} className="w-full max-w-sm space-y-4">
          <h2 className="text-2xl font-semibold text-slate-900">Sign in</h2>

          {expired && (
            <p className="text-sm text-amber-600 bg-amber-50 border border-amber-200 rounded-lg px-3 py-2">
              Session expired. Please log in again.
            </p>
          )}
          {error && <p className="text-sm text-red-600">{error}</p>}

          <input
            type="email"
            placeholder="Email"
            required
            className="w-full border border-slate-200 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
          <input
            type="password"
            placeholder="Password"
            required
            className="w-full border border-slate-200 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />

          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? "Signing in..." : "Sign In"}
          </Button>

          <p className="text-sm text-slate-500 text-center">
            Don't have an account? <Link to="/register" className="text-blue-600">Create account</Link>
          </p>
        </form>
      </div>
    </div>
  );
}
