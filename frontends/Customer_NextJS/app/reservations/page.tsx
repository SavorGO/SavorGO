"use client";
import React, { useState } from "react";

const SetTable = () => {
  const [formData, setFormData] = useState({
    name: "",
    phone: "",
    branch: "",
    date: "",
    time: "",
    guests: "1",
  });

  const [errors, setErrors] = useState({} as Record<string, string>);
  const [successMessage, setSuccessMessage] = useState("");

  const validateForm = () => {
    let newErrors: Record<string, string> = {};

    if (!formData.name.trim()) newErrors.name = "Họ tên không được để trống.";
    if (!formData.phone.match(/^[0-9]{10,11}$/))
      newErrors.phone = "Số điện thoại không hợp lệ.";
    if (!formData.branch) newErrors.branch = "Vui lòng chọn chi nhánh.";
    if (!formData.date) newErrors.date = "Vui lòng chọn ngày.";
    if (new Date(formData.date) < new Date())
      newErrors.date = "Không được chọn ngày trong quá khứ.";
    if (!formData.time) newErrors.time = "Vui lòng chọn thời gian.";
    if (parseInt(formData.guests) <= 0)
      newErrors.guests = "Số lượng khách phải lớn hơn 0.";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      // Gửi dữ liệu tới API
      const response = await fetch("/api/reservations", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      const data = await response.json();
      if (response.ok) {
        setSuccessMessage(data.message);
        setFormData({
          name: "",
          phone: "",
          branch: "",
          date: "",
          time: "",
          guests: "1",
        });
      } else {
        setErrors(data);
      }
    }
  };

  return (
    <div className="relative bg-cover bg-center h-[800px] w-[1170px]" style={{ backgroundImage: "url('/hero.png')" }}>
      <div className="absolute inset-0 bg-black bg-opacity-50 flex justify-center items-center">
        <div className="bg-black bg-opacity-70 p-8 rounded-lg w-full max-w-2xl text-white border border-yellow-500">
          <h2 className="text-center text-2xl font-bold mb-4">ĐẶT BÀN</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Họ tên */}
            <div>
              <label className="block text-sm">Họ tên *</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="w-full p-2 rounded bg-gray-800 border border-gray-600"
                placeholder="Nhập họ tên"
              />
              {errors.name && <p className="text-red-400 text-sm">{errors.name}</p>}
            </div>

            {/* Số điện thoại */}
            <div>
              <label className="block text-sm">Số điện thoại *</label>
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                className="w-full p-2 rounded bg-gray-800 border border-gray-600"
                placeholder="Nhập số điện thoại"
              />
              {errors.phone && <p className="text-red-400 text-sm">{errors.phone}</p>}
            </div>

            {/* Ngày & Giờ */}
            <div className="flex space-x-4">
              <div className="w-1/2">
                <label className="block text-sm">Ngày *</label>
                <input
                  type="date"
                  name="date"
                  value={formData.date}
                  onChange={handleChange}
                  className="w-full p-2 rounded bg-gray-800 border border-gray-600"
                />
                {errors.date && <p className="text-red-400 text-sm">{errors.date}</p>}
              </div>
              <div className="w-1/2">
                <label className="block text-sm">Thời gian *</label>
                <input
                  type="time"
                  name="time"
                  value={formData.time}
                  onChange={handleChange}
                  className="w-full p-2 rounded bg-gray-800 border border-gray-600"
                />
                {errors.time && <p className="text-red-400 text-sm">{errors.time}</p>}
              </div>
            </div>

            {/* Số lượng khách */}
            <div>
              <label className="block text-sm">Số lượng khách dự kiến *</label>
              <select
                name="guests"
                value={formData.guests}
                onChange={handleChange}
                className="w-full p-2 rounded bg-gray-800 border border-gray-600"
              >
                {[...Array(20)].map((_, i) => (
                  <option key={i} value={i + 1}>
                    {i + 1}
                  </option>
                ))}
              </select>
              {errors.guests && <p className="text-red-400 text-sm">{errors.guests}</p>}
            </div>

            {/* Nút Đặt Bàn */}
            <button type="submit" className="w-full bg-yellow-500 text-black py-2 rounded mt-4 font-bold hover:bg-yellow-600">
              ĐẶT BÀN
            </button>
          </form>

          {successMessage && (
            <div className="mt-4 text-green-500 text-center">
              {successMessage}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SetTable;
