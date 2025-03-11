import React from 'react'
import Hero from './Hero'
import { BannerDataType, CategoriesBannerDataType, productType } from '@/lib/interface'
import Featured from './Featured'
import Categories from './Categories'
import OurStory from './OurStory'
interface LandingPageProps {
  bannerData: BannerDataType
  featuredProducts: productType[]
  categoriesData: CategoriesBannerDataType[]
}
const LandingPage: React.FC<LandingPageProps> = ({ bannerData, featuredProducts, categoriesData }) => {
  return (
    <section className='flex flex-col w-full'>
      <Hero />
      <OurStory/>
      <Featured  />
    </section>
  )
}

export default LandingPage