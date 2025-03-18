import { NextResponse } from 'next/server';

const menuItems = [
    { id: "1", name: "Phở bò", price: 50000, image: "/images/buncha.jpg", description: "Phở bò thơm ngon đặc biệt" },
    { id: "2", name: "Bún chả", price: 60000, image: "/images/buncha.jpg", description: "Bún chả Hà Nội ngon tuyệt" },
    { id: "3", name: "Bánh mì", price: 20000, image: "/images/buncha.jpg", description: "Bánh mì Sài Gòn giòn rụm" },
];

export async function GET(req: Request, { params }: { params: { id: string } }) {
    const { id } = params;
    const menuItem = menuItems.find(item => item.id === id);

    if (!menuItem) {
        return NextResponse.json({ error: "Không tìm thấy món ăn" }, { status: 404 });
    }

    return NextResponse.json(menuItem);
}
