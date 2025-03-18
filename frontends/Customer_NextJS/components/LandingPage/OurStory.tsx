import Link from "next/link";
import React from "react";
import { Button } from "../ui/button";

const OurStory = () => {
  return (
    <div className="font-DMSans relative items-center flex flex-col mt-12">
      {/* Thay bannerData.image bằng logo.png */}
      <div className="w-80 mb-4">
        <img src="story1.png" alt="" />
      </div>
      {/* Nội dung chính */}
      <div className="max-w-5xl mx-auto mt-8 grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Câu chuyện của chúng tôi */}
        <div>
          <h3 className="text-xl font-bold mb-4">CÂU CHUYỆN CỦA CHÚNG TÔI</h3>
          <p className="text-xm leading-relaxed">
            Đó là một thế giới tái sinh, một thế giới quan gai góc, thô kệch &
            bụi bặm bắt đầu từ phong cách thiết kế không gian bằng kim loại và
            những tone màu tối, những hình vẽ phác hoạ “loài người Warning Zone”
            dù đang phải chống chọi lại sự khắc nghiệt đến từ hậu quả của những
            cuộc chiến tranh để lại, nhưng họ vẫn sống với một ý chí mạnh mẽ,
            kiên trì và tích cực tạo ra giá trị sống ý nghĩa mỗi ngày nhằm kiến
            tạo thế giới này trở nên tốt đẹp hơn.
          </p>
          <p className="text-xm leading-relaxed mt-4">
            Và phát triển bền vững – luôn là kim chỉ nam trong cách vận hành và
            phát triển của Warning Zone, chúng tôi đem đến những món ăn ngon
            lành từ nguồn thực phẩm sạch, những khoảnh khắc trải nghiệm tuyệt
            vời từ những ý tưởng & những con người Warning Zone với ý niệm tái
            sinh cuộc sống từ trong huyết mạch. Cảm ơn các bạn đã chọn Warning
            Zone hôm nay!
          </p>
        </div>

        {/* Giá trị cốt lõi */}
        <div>
          <h3 className="text-xl font-bold mb-4">GIÁ TRỊ CỐT LÕI</h3>
          <ul className="text-xm space-y-3">
            <li>
              <span className="text-black font-bold">1 - THIẾT KẾ -</span> Kiến
              tạo tính cách & nhận diện thương hiệu của Warning Zone.
            </li>
            <li>
              <span className="text-black font-bold">2 - CON NGƯỜI -</span> được
              xác định là yếu tố nòng cốt để ưu tiên cho việc phát triển bền
              vững, bao gồm quá trình phát triển & bảo vệ.
            </li>
            <li>
              <span className="text-black font-bold">
                3 - SẢN PHẨM CHẤT LƯỢNG -
              </span>{" "}
              Warning Zone luôn cố gắng tạo ra dấu ấn riêng bằng khẩu vị đặc
              trưng thông qua các món ăn dành cho thực khách với nguồn thực phẩm
              chất lượng cao & quá trình chế biến được đảm bảo theo tiêu chuẩn
              vệ sinh an toàn thực phẩm.
            </li>
            <li>
              <span className="text-black font-bold">
                4 - HÀNH ĐỘNG NHÂN VĂN -
              </span>{" "}
              tạo điều kiện phát huy tinh thần & hành động hướng đến việc bảo vệ
              môi trường, phát triển xã hội & con người.
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default OurStory;
