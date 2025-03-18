'use client'
import React, { useState } from 'react';
import Modal from 'react-modal';
import Image from 'next/image';

const images = [
  '/images/khonggian1.jpg', '/images/khonggian2.jpg', '/images/khonggian3.jpg',
  '/images/khonggian4.jpg', '/images/khonggian5.jpg', '/images/khonggian6.jpg',
  '/images/khonggian7.jpg', '/images/khonggian8.jpg', '/images/khonggian9.jpg'
];

const Delivery = () => {
    const [modalIsOpen, setModalIsOpen] = useState(false);
    const [currentIndex, setCurrentIndex] = useState(0);

    const openModal = (index: number) => {
        setCurrentIndex(index);
        setModalIsOpen(true);
    };

    const closeModal = () => {
        setModalIsOpen(false);
    };

    const nextImage = () => {
        setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
    };

    const prevImage = () => {
        setCurrentIndex((prevIndex) => (prevIndex - 1 + images.length) % images.length);
    };

    return (
        <div className="container mx-auto py-10">
            <h2 className="text-center text-2xl font-bold text-orange-500">KHÔNG GIAN QUÁN</h2>
            <div className="grid grid-cols-3 gap-6 justify-center mt-10">
                {images.map((src, index) => (
                    <div 
                        key={index} 
                        className="relative w-[395px] h-[266px] cursor-pointer" 
                        onClick={() => openModal(index)}
                    >
                        <Image 
                            src={src} 
                            alt={`image-${index}`} 
                            width={360}
                            height={266}
                            className="rounded-lg object-cover"
                        />
                    </div>
                ))}
            </div>
            
            <Modal 
                isOpen={modalIsOpen} 
                onRequestClose={closeModal} 
                className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-75"
            >
                <div className="relative flex items-center justify-center">
                    <button 
                        className="absolute left-4 bg-white p-2 rounded-full shadow-md" 
                        onClick={prevImage}
                    >
                        &#8592;
                    </button>
                    <div className="relative w-[80vw] h-[80vh]">
                        <Image 
                            src={images[currentIndex]} 
                            alt="selected" 
                            layout="fill"
                            objectFit="contain"
                            className="rounded-lg"
                        />
                    </div>
                    <button 
                        className="absolute right-4 bg-white p-2 rounded-full shadow-md" 
                        onClick={nextImage}
                    >
                        &#8594;
                    </button>
                    <button 
                        className="absolute top-4 right-4 bg-red-500 text-white p-2 rounded-full" 
                        onClick={closeModal}
                    >
                        ✕
                    </button>
                </div>
            </Modal>
        </div>
    );
};

export default Delivery;
