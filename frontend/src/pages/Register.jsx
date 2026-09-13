import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerUser } from "../api/auth";
import Button from "../components/Button";

export default function Register() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        fullName: "",
        email: "",
        password: "",
        confirmPassword: "",
    });

    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (form.password.length < 8) {
            setError("Password must be at least 8 characters.");
            return;
        }
        if (form.password !== form.confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        setLoading(true);
        try {
            await registerUser({
                fullName: form.fullName,
                email: form.email,
                password: form.password,
            });

            setSuccess(true);
            setTimeout(() => navigate("/login"), 1200);
        } catch (err) {
            setError(
                err.response?.status === 409
                    ? "An account with this email already exists."
                    : "Registration failed. Please try again."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex">
            <div className="hidden lg:flex w-1/2 bg-slate-900 text-white flex-col justify-center px-16">
                <h1 className="text-3xl font-bold mb-2">
                    Interview Prep Q&A Builder
                </h1>

                <p className="text-slate-400">
                    Build your knowledge. Track your preparation.
                </p>
            </div>

            <div className="flex-1 flex items-center justify-center px-6">
                <form
                    onSubmit={handleSubmit}
                    className="w-full max-w-sm space-y-4"
                >
                    <h2 className="text-2xl font-semibold text-slate-900">
                        Create account
                    </h2>

                    {success && (
                        <p className="text-sm text-green-600 bg-green-50 border border-green-200 rounded-lg px-3 py-2">
                            Account created! Redirecting to sign in...
                        </p>
                    )}
                    {error && (
                        <p className="text-sm text-red-600">
                            {error}
                        </p>
                    )}

                    <input
                        type="text"
                        placeholder="Full Name"
                        required
                        className="w-full border border-slate-200 rounded-lg px-4 py-2 text-sm"
                        value={form.fullName}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                fullName: e.target.value,
                            })
                        }
                    />

                    <input
                        type="email"
                        placeholder="Email"
                        required
                        className="w-full border border-slate-200 rounded-lg px-4 py-2 text-sm"
                        value={form.email}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                email: e.target.value,
                            })
                        }
                    />

                    <input
                        type="password"
                        placeholder="Password"
                        required
                        className="w-full border border-slate-200 rounded-lg px-4 py-2 text-sm"
                        value={form.password}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                password: e.target.value,
                            })
                        }
                    />

                    <input
                        type="password"
                        placeholder="Confirm Password"
                        required
                        className="w-full border border-slate-200 rounded-lg px-4 py-2 text-sm"
                        value={form.confirmPassword}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                confirmPassword: e.target.value,
                            })
                        }
                    />

                    <Button type="submit" className="w-full" disabled={loading || success}>
                        {loading ? "Creating account..." : "Create Account"}
                    </Button>

                    <p className="text-sm text-slate-500 text-center">
                        Already have an account?{" "}
                        <Link
                            to="/login"
                            className="text-blue-600"
                        >
                            Sign in
                        </Link>
                    </p>
                </form>
            </div>
        </div>
    );
}
