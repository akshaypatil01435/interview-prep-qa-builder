import Card from "./Card";

export default function StatCard({
                                     label,
                                     value,
                                     icon: Icon,
                                     trend,
                                 }) {
    return (
        <Card className="flex items-center justify-between">
            <div>
                <p className="text-sm text-slate-500">{label}</p>

                <p className="text-2xl font-semibold text-slate-900 mt-1">
                    {value}
                </p>

                {trend && (
                    <p className="text-xs text-green-600 mt-1">
                        {trend}
                    </p>
                )}
            </div>

            {Icon && (
                <Icon className="w-8 h-8 text-blue-600" />
            )}
        </Card>
    );
}
