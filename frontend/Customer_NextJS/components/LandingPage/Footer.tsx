import React from 'react';

const Footer = () => {
    return (
        <footer className="bg-black text-white w-full p-6 md:p-10 text-sm md:text-base font-light font-Outfit mt-24">
            <div className="container mx-auto grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* Cột 1 */}
                <div>
                    <h2 className="text-yellow-400 font-bold text-lg">SAVORGO</h2>
                    <p className="mt-2">CÔNG TY CỔ PHẦN SAVORGO</p>
                    <p className="mt-2">Địa chỉ: 221 Nguyễn Thái Học, Phường Cầu Ông Lãnh, Quận 1, TP Hồ Chí Minh, Việt Nam.</p>
                    <p className="mt-2">Mail: nguyenquocthai@gmail.com.com</p>
                    <p className="mt-2">Hotline: 0358097747</p>
                </div>

                {/* Cột 2 */}
                <div>
                    <h2 className="font-bold text-lg">Giấy chứng nhận CSĐĐKATTP</h2>
                    <p className="mt-2">Số 1540/2020/ATTP-CNĐK do Ban Quản lý An toàn thực phẩm thành phố Hồ Chí Minh cấp ngày 05/05/2020</p>
                    
                    <h2 className="font-bold text-lg mt-4">Giấy chứng nhận ĐKKD</h2>
                    <p className="mt-2">Số 0315952588 do Sở Kế Hoạch và Đầu tư cấp ngày 09/10/2019</p>
                </div>

                {/* Cột 3 */}
                <div>
                    <h2 className="text-yellow-400 font-bold text-lg">ĐĂNG KÝ NHẬN TIN</h2>
                    <p className="mt-2">Đăng ký nhận tin tức và ưu đãi mới nhất từ Warning Zone.</p>
                    <div className="mt-4 border border-yellow-400 p-2 flex items-center justify-between cursor-pointer">
                        <span className="text-gray-400">Email</span>
                        <span className="text-yellow-400">→</span>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
