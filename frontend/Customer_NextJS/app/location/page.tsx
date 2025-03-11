"use client"
import React, { useState, useEffect } from 'react'

const Location = () => {
  const [selectedLocation, setSelectedLocation] = useState('hcm')
  const [activeButton, setActiveButton] = useState('hcm')
  const [selectedAddress, setSelectedAddress] = useState('')

  const hcmLocations = [
    { address: '109 Võ Nguyên Giáp (Song Hành Xa Lộ Hà Nội), P.Thảo Điền, TP.Thủ Đức', phone: '1900 6096', hours: '16:00 - 01:00', image: '/images/hcm1.jpg' },
    { address: '123 Lê Văn Sỹ, Quận Phú Nhuận', phone: '1900 6096', hours: '16:00 - 01:00', image: '/images/hcm2.jpg' },
    { address: '456 Trường Chinh, Quận Tân Bình', phone: '1900 6096', hours: '16:00 - 01:00', image: '/images/hcm3.png' },
    { address: '789 Nguyễn Văn Linh, Quận 7', phone: '1900 6096', hours: '16:00 - 01:00', image: '/images/hcm4.jpg' },
    { address: '321 Lê Đức Thọ, Quận Gò Vấp', phone: '1900 6096', hours: '16:00 - 01:00', image: '/images/hcm5.jpg' },
    { address: '654 Lê Lợi, Quận 1', phone: '1900 6096', hours: '16:00 - 01:00', image: '/images/hcm6.jpg' }
  ]

  const phanThietLocations = [
    { address: '33 Nguyễn Đình Chiểu, Quận 1', phone: '1900 6096', hours: '16:00 - 1:00', image: '/images/phanthiet1.png' },
    { address: '45 Trần Hưng Đạo, Quận 1', phone: '1900 6096', hours: '16:00 - 1:00', image: '/images/phanthiet2.jpg' },
    { address: '789 Lê Hồng Phong, Quận 1', phone: '1900 6096', hours: '16:00 - 1:00', image: '/images/phanthiet.jpg' }
  ]

  const nhaTrangLocations = [
    { address: 'Lẩu 5, 31 Lê Thị Riêng, Quận 1', phone: '1900 6096', hours: '16:00 - 1:00', image: '/images/nhatrang1.jpg' },
    { address: '123 Nguyễn Văn Cừ, Quận 1', phone: '1900 6096', hours: '16:00 - 1:00', image: '/images/nhatrang2.jpg' },
    { address: '456 Trần Phú, Quận 1', phone: '1900 6096', hours: '16:00 - 1:00', image: '/images/nhatrang.jpg' }
  ]

  const handleLocationClick = (location) => {
    setSelectedLocation(location)
    setActiveButton(location)
  }

  const handleBranchClick = (address) => {
    const googleMapsUrl = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(address)}`
    window.open(googleMapsUrl, '_blank')
  }

  useEffect(() => {
    // Hiển thị các chi nhánh ở TP. Hồ Chí Minh khi mới chạy chương trình
    setSelectedLocation('hcm')
    setActiveButton('hcm')
  }, [])

  return (
    <div>
      <div className="flex justify-between items-end border-b-2  mt-8">
        <div
          className={`w-1/3 bg-white shadow-md p-6 mb-0 cursor-pointer ${activeButton === 'hcm' ? 'border-b-4 border-yellow-500' : ''}`}
          onMouseEnter={(e) => e.currentTarget.classList.add('opacity-80')}
          onMouseLeave={(e) => e.currentTarget.classList.remove('opacity-80')}
          onClick={() => handleLocationClick('hcm')}
        >
          <h3 className="text-lg font-bold mb-2">TP HỒ CHÍ MINH</h3>
        </div>

        <div
          className={`w-1/3 bg-white shadow-md p-6 mb-0 cursor-pointer ${activeButton === 'phan-thiet' ? 'border-b-4 border-yellow-500' : ''}`}
          onMouseEnter={(e) => e.currentTarget.classList.add('opacity-80')}
          onMouseLeave={(e) => e.currentTarget.classList.remove('opacity-80')}
          onClick={() => handleLocationClick('phan-thiet')}
        >
          <h3 className="text-lg font-bold mb-2">PHAN THIẾT</h3>
        </div>

        <div
          className={`w-1/3 bg-white shadow-md p-6 mb-0 cursor-pointer ${activeButton === 'nha-trang' ? 'border-b-4 border-yellow-500' : ''}`}
          onMouseEnter={(e) => e.currentTarget.classList.add('opacity-80')}
          onMouseLeave={(e) => e.currentTarget.classList.remove('opacity-80')}
          onClick={() => handleLocationClick('nha-trang')}
        >
          <h3 className="text-lg font-bold mb-2">NHA TRANG</h3>
        </div>
      </div>

      {selectedLocation === 'hcm' && (
        <div className="mt-6 grid grid-cols-3 gap-4">
          {hcmLocations.map((location, index) => (
           <div
           key={index}
           className="bg-white rounded-lg shadow-md p-6 h-96 cursor-pointer"
           onClick={() => handleBranchClick(location.address)}
         >
              <img src={location.image} alt={`Chi nhánh ${index + 1}`} className="mb-4 rounded-lg w-full h-300 object-cover" />
              <p className="mb-2">{location.address}</p>
              <p className="mb-2">{location.phone}</p>
              <p className="mb-0">{location.hours}</p>
            </div>
          ))}
        </div>
      )}

      {selectedLocation === 'phan-thiet' && (
        <div className="mt-6 grid grid-cols-3 gap-4">
          {phanThietLocations.map((location, index) => (
            <div key={index} className="bg-white rounded-lg shadow-md p-6 h-96 cursor-pointer" onClick={() => handleBranchClick(location.address)}>
              <img src={location.image} alt={`Chi nhánh Phan Thiết ${index + 1}`} className="mb-4 rounded-lg w-full h-300 object-cover" />
              <p className="mb-2">{location.address}</p>
              <p className="mb-2">{location.phone}</p>
              <p className="mb-0">{location.hours}</p>
            </div>
          ))}
        </div>
      )}

      {selectedLocation === 'nha-trang' && (
        <div className="mt-6 grid grid-cols-3 gap-4">
          {nhaTrangLocations.map((location, index) => (
            <div key={index} className="bg-white rounded-lg shadow-md p-6 h-96 cursor-pointer" onClick={() => handleBranchClick(location.address)}>
              <img src={location.image} alt={`Chi nhánh Nha Trang ${index + 1}`} className="mb-4 rounded-lg w-full h-300 object-cover" />
              <p className="mb-2">{location.address}</p>
              <p className="mb-2">{location.phone}</p>
              <p className="mb-0">{location.hours}</p>
            </div>
          ))}
        </div>
      )}

{selectedAddress && (
  <div className="mt-6">
    <h4 className="text-lg font-bold">Bản đồ chi nhánh:</h4>
    <iframe
      src={`https://www.google.com/maps/embed/v1/place?key=AIzaSyBfotWZUyOZgA4OeYy4bxGb72NvPzZAYSA=${encodeURIComponent(selectedAddress)}`}
      width="600"
      height="450"
      style={{ border: 0 }}
      allowFullScreen
      loading="lazy"
    ></iframe>
  </div>
)}
    </div>
  )
}

export default Location
