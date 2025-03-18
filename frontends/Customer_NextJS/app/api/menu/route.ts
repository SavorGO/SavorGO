export async function GET() {
    try {
        const response = await fetch(
            'http://127.0.0.1:8000/api/menus?page=1&size=20&sortBy=id&sortDirection=asc&statusFilter=available'
        );

        if (!response.ok) {
            throw new Error('Failed to fetch menu data');
        }

        const data = await response.json();

        return Response.json(data, { status: 200 });
    } catch (error: unknown) {
        if (error instanceof Error) {
            return Response.json({ error: error.message }, { status: 500 });
        }
        return Response.json({ error: 'Unknown error occurred' }, { status: 500 });
    }
}
