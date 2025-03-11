import { NextResponse } from "next/server";

// Danh sách chi nhánh hợp lệ
const VALID_BRANCHES = ["Chi nhánh 1", "Chi nhánh 2", "Chi nhánh 3"];

export async function POST(req: Request) {
  try {
    const body = await req.json();
    const { name, phone, branch, date, time, guests } = body;

    // Kiểm tra ràng buộc nhập liệu
    if (!name || name.length < 3) {
      return NextResponse.json({ error: "Họ tên phải có ít nhất 3 ký tự." }, { status: 400 });
    }

    if (!phone || !/^\d{10}$/.test(phone)) {
      return NextResponse.json({ error: "Số điện thoại không hợp lệ (phải có 10 chữ số)." }, { status: 400 });
    }

    if (!VALID_BRANCHES.includes(branch)) {
      return NextResponse.json({ error: "Chi nhánh không hợp lệ." }, { status: 400 });
    }

    if (!date || isNaN(Date.parse(date)) || new Date(date) < new Date()) {
      return NextResponse.json({ error: "Ngày không hợp lệ hoặc không thể là ngày trong quá khứ." }, { status: 400 });
    }

    if (!time || !/^\d{2}:\d{2}$/.test(time)) {
      return NextResponse.json({ error: "Thời gian không hợp lệ." }, { status: 400 });
    }

    if (!guests || isNaN(guests) || guests < 1 || guests > 20) {
      return NextResponse.json({ error: "Số lượng khách phải từ 1 đến 20." }, { status: 400 });
    }

    // Nếu hợp lệ, lưu vào DB (giả lập)
    return NextResponse.json({ message: "Đặt bàn thành công rồi!", data: body }, { status: 201 });

  } catch (error) {
    return NextResponse.json({ error: "Lỗi máy chủ." }, { status: 500 });
  }
}
