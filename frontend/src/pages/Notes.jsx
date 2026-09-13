import { useState } from "react";
import AppLayout from "../layouts/AppLayout";
import Card from "../components/Card";

export default function Notes() {
    const [notes] = useState([]);

    return (
        <AppLayout title="My Notes">
            {notes.length === 0 ? (
                <p className="text-slate-400 text-center py-20">
                    No notes yet. Add notes from any question's detail page.
                </p>
            ) : (
                <div className="space-y-3">
                    {notes.map((n) => (
                        <Card key={n.id}>
                            <p className="font-medium text-slate-800">
                                {n.question?.questionText}
                            </p>

                            <p className="text-sm text-slate-500 mt-1">
                                {n.content}
                            </p>
                        </Card>
                    ))}
                </div>
            )}
        </AppLayout>
    );
}